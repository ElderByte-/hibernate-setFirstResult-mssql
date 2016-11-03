# hibernate-setFirstResult-mssql [HHH-11194](https://hibernate.atlassian.net/browse/HHH-11194)
A test-case to show that `session.setFirstResult` works on a MSSQL 2000 Server with **Hibernate 4** but no longer with **Hibernate 5**.


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
