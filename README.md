# hibernate-setFirstResult-mssql [HHH-11194](https://hibernate.atlassian.net/browse/HHH-11194)
A test-case to show that `session.setFirstResult` works on a MSSQL 2000 Server with **Hibernate 4** but no longer with **Hibernate 5**.

As this test case illustrates, the exact same code runs with hibernate `4.3.11.Final`, but does not with `5.2.4.Final`.


## How to reproduce the regression:

You need a MSSQL Server which supports the (old) Dialect `org.hibernate.dialect.SQLServerDialect`, since this dialect is responsible for the issue. It has been tested with a MSSQL Server 2000 in our case.

If you run the test code in this repo, using `'org.hibernate', name: 'hibernate-core', version: '4.3.11.Final'` it will work as expected.
Switch this out with `compile group: 'org.hibernate', name: 'hibernate-core', version: '5.2.4.Final'` and the test case will throw a hibernate exception:

```
java.lang.UnsupportedOperationException: query result offset is not supported
```


## Observations so far

* While the MSSQL Server 2000 does indeed not natively support query offsets, hibernate 4 had a workaround of some kind.
* Since the Dialects got heavily refactored in hibernate 5, especally the offset and limit part, we assume the regression was introduced there.
* Since version 5 the SQLServerDialect uses the class TopLimitHandler for handling limit queries. The check for any first row selecton introduced there lead to the error message.
* It seems that it still works if you just remove the check.

* Involved hibernate classes:
  * SQLServerDialect 
  * TopLimitHandler
  * AbstractLimitHandler (bindLimitParameters)
  * Loader

## Issue 'bindLimitParametersFirst'

We've implemented a 'quick and dirty' fix by implementing a custom SQLServerDialect in which we override the ToplimitHandler and just removed the `LimitHelper.hasFirstRow( selection )` check in the `processSql`. At first it seemed to work. However, in our productive environment I've noticed that the paging again does not work for a specific entity.

While there was no RunTimeException (like in the first issue) the `content` of the pagerequest was just empty.

We've noticed via the MSSQL profiler that the two parameters of the query were swapped! The value of the `top` clause was used in the `where` clause and vice versa.

We've tried to reproduce the issue with our domain entity in this repo but to no avail. Somehow it just works here while it does not in our productive environment. Now there are of course some differences between these two projects:

* In our productive environment we use Spring and JPA on top of hibernate, while we implemented just the minimum hibernate configurations in this repo to narrow the search for the issues.
* Maybe more...

After some digging and debugging of the hibernate code we've stumbled upon the `bindLimitParametersFirst` property in the `TopLimitHandler`. It was initialized as `false` in our custom TopLimitHandler, **like in the official `SQLServerDialect` class**.

> The issue seems to be resolved when setting the property to `true`.

**This begs two questions:**

**First**, if it's correct that the `TopLimitHandler` in the official `SQLServerDialect` class is initialized with the constructor parameters `(false, false)` or if the `bindLimitParametersFirst` parameter should be set to true.

And **second**, why does it work in this repo?

To answer the second question the following code seems to be of importance:

class `org.hibernate.loader`, method `prepareQueryStatement`:

```
1. int col = 1;
2.			//TODO: can we limit stored procedures ?!
3.			col += limitHandler.bindLimitParametersAtStartOfQuery( selection, st, col );
4.
5.			if ( callable ) {
6.				col = dialect.registerResultSetOutParameter( (CallableStatement) st, col );
7.			}
8.
9.			col += bindParameterValues( st, queryParameters, col, session );
10.
11.			col += limitHandler.bindLimitParametersAtEndOfQuery( selection, st, col );
12.
13.			limitHandler.setMaxRows( selection, st );
```

This code snippet binds the values to the query. Variable `col` indicates the index of the parameter-placeholder to bind. What happens:

**CASE test Repo**  
Initial position: `bindLimitParametersFirst` set to `false`

* line 3 does not bind any parameter since `bindLimitParametersFirst` is `false`. `col` stays at `1`
* line 9 does not bind any parameter since the list `collectedParameterSpecifications` in the class `QueryTranslatorImpl` is emtpy. `col` stays at `1`
* line 11 **does** bind the limit value (internally it binds if `!bindLimitParametersFirst` is true). The value is bound at the correct index (1)

**CASE productive environment**  
Initial position: `bindLimitParametersFirst` set to `false`

* line 3 does not bind any parameter since `bindLimitParametersFirst` is `false`. `col` stays at `1`
* line 9 **does** bind the first parameter since the list `collectedParameterSpecifications` in the class `QueryTranslatorImpl` contains one element (the value of the where clause). `col` becomes `2`
* line 11 **does** bind the limit value (internally it binds if `!bindLimitParametersFirst` is true). The value is bound at the wrong index (2)

**CASE productive environment fixed**  
Initial position: `bindLimitParametersFirst` set to `true`

* line 3 **does** bind any parameter since `bindLimitParametersFirst` is `true`. `col` becomes `2`
* line 9 **does** bind the first parameter since the list `collectedParameterSpecifications` in the class `QueryTranslatorImpl` contains one element (the value of the where clause). `col` becomes `3`
* line 11 does not bind since `!bindLimitParametersFirst` is false.

