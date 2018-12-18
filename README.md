### Requirements
JDK 8 or above
SBT 0.13.17

### Assumptions
1) no persistence of request & results of comparison - during life of service results of comparison will be 
kept in memory and disappear after restart
2) In case multiple data entries will be submited with the same IDs - data will be overwritten and 
during comparison we will rely on latest entries corresponding to queryId
3) settings of parallelism will be defined by cfg file via deployment section for particular actors
4) API endpoints will receive & return data in plain json format 
5) no auth & security
6) ID of query will be treated as String - i.e. UUID or Integer based ids will be accepted
 

### How to build
```bash
sbt assembly
```
will create fat jar with all dependencies at `target/scala-2.11/json_diff-assembly-1.0.0.jar`

### How to run

1) edit cfg file at conf/app.conf to change binded ip & port
2) execute start.sh by invoking sh start.sh
3) use following endpoints to validate requests:

```bash
# To verify that it is up & running
curl -w "\n" -XGET 'http://localhost:8080/healthcheck'

# To submit some data:
curl  -v --request POST --data-binary "@data_samples/lena.png" http://localhost:8080/v1/diff/id123/left
curl  -v --request POST --data-binary "@data_samples/glider.png" http://localhost:8080/v1/diff/id123/right

# To check result:
curl  -w "\n" -XGET http://localhost:8080/v1/diff/id123

```

### Notes

NOTE: in case you have encrypted home folder or mounted volume(s)
you may need to run following commands before assembling and after every 
```bash
sbt clean:
```

```bash
rm -rf ./target
mkdir /tmp/`echo $$`
ln -s /tmp/`echo $$` ./target
```
