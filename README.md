# DataShops

## What is DataShops
DataShops is an enterprise-level PaaS platform that provides enterprises with a full range of product services such
 as workflow scheduling, data development, data integration and data services. One-stop development and management interface helps reduce the cost of big data development and maintenance. Enterprises focus on the mining and exploration of data value
## Demo
* [demo](https://demo.datashops.cn)
* [中文文档](https://github.com/NextMark/datashops/blob/master/docs/README-zh.md)


## Features
* One-stop scheduling and development
  * DataShops provides powerful scheduling functions
    * Support the mechanism of triggering tasks based on time and dependency
    * Supports tens of millions of tasks per day, and runs accurately and on time according to the DAG relationship.
    * Support multiple scheduling cycles in minutes, hours, days, weeks and months.
  * Fully managed service, no need to care about scheduling server resources.
  * Provide isolation function to ensure that tasks between different tenants will not affect each other.
* DataShops supports multiple task types
  * Offline task
    * Hive
    * Spark
    * Flink
    * Shell
    * Python
    * ClickHouse
  * Streaming task
    * Flink SQL
    * Kafka -> HDFS
    * Kafka -> Hive
  * Data integration
    * Hive -> MySQL
    * MySQL -> Hive
* Visual development
  * DataShops provides visual code development, workflow designer pages, no need to match any development tools, simple drag and drop and development, you can complete complex data analysis tasks

* Monitoring alarm
  * The operation and maintenance center provides a visual task monitoring and management tool, and supports the display of the overall situation of the task in the form of a DAG diagram.
  * You can easily configure various alarm methods, and notify relevant personnel in time when task errors occur to ensure the normal operation of the business.

## Architecture
DataShops is a distributed scheduling system, which is divided into three components: master, worker, and api.
 
 The internal communication is through grpc, and multiple masters do load balance.
![architecture](https://github.com/NextMark/datashops/raw/master/docs/ds-architecture.jpg)

## Deployment document
[Deployment Document](https://github.com/NextMark/datashops/wiki/%E9%83%A8%E7%BD%B2%E6%96%87%E6%A1%A3)
## Contact

If you have any questions in use, please feel free to give me feedback.
 
 You can use the following contact methods to communicate:

* shiwei138@163.com

![qr-code](https://github.com/NextMark/datashops/raw/master/docs/gzh_qrcode.jpg)
