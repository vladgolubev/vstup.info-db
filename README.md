##### This app parses data from [vstup.info](http://vstup.info) and stores it in [SQLite](http://sqlite.org) database

![How it looks](http://i.imgur.com/bprq2UM.png)

Just change *year* and *direction number* in ```Main.java```
```java
Parser parser = new Parser(2013, "6.050102");
parser.parse();
```

The greatest thing about this is that you can perform various **SQL queries** with the data.

```sql
SELECT * FROM parsed2013_6050103 WHERE city = "Київ" OR city = "Львів" AND passing_score > 700 ORDER BY places DESC;
```
![Result](http://i.imgur.com/QRZKvIX.png)
