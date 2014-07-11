##### This app parses data from [vstup.info](http://vstup.info) and stores it in [SQLite](http://sqlite.org) database

Just change *year* and *direction number* in ```Main.java```
```java
Parser parser = new Parser(2013, "6.050102");
parser.parse();
```

Obviously, you can perform various **SQL queries** with the data.

```sql
SELECT * FROM parsed2013_6050103 WHERE city = "Київ" OR city = "Львів" AND passing_score > 700 ORDER BY places DESC;
```
