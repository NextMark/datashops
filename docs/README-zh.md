# DataShops

## DataShops是什么
DataShops - 数据工厂是一个企业级PaaS平台，为企业提供工作流调度、数据开发、数据集成和数据服务等全方位的产品服务，一站式开发管理的界面，降低大数据开发和维护成本，帮助企业专注于数据价值的挖掘和探索
## 演示地址
[演示地址](https://demo.datashops.cn)

## 功能
* 一站式调度、开发
  * DataShops提供强大的调度功能
    * 支持根据时间、依赖关系，进行任务触发的机制
    * 支持每日千万级别的任务，根据DAG关系准确、准时地运行。
    * 支持分钟、小时、天、周和月多种调度周期。
  * 完全托管的服务，无需关心调度的服务器资源问题。
  * 提供隔离功能，确保不同租户之间的任务不会相互影响。
* DataShops支持多种任务类型
  * 离线作业
    * Hive
    * Spark
    * Flink
    * Shell
    * Python
    * ClickHouse
  * 流式作业
    * Flink SQL
    * Kafka -> HDFS
    * Kafka -> Hive
  * 数据导入
    * Hive -> MySQL
    * MySQL -> Hive
* 可视化开发
  * DataShops提供可视化的代码开发、工作流设计器页面，无需搭配任何开发工具，简单拖拽和开发，即可完成复杂的数据分析任务

* 监控告警
  * 运维中心提供可视化的任务监控管理工具，支持以DAG图的形式展示任务运行时的全局情况，
  * 您可以方便地配置各类报警方式，任务发生错误可及时通知相关人员，保证业务正常运行。

## 架构设计
DataShops是一个分布式调度系统，分为master、worker、api三大组件，内部通过grpc通信，多个master做load balance
## 部署文档
[部署文档](https://github.com/NextMark/datashops/wiki/%E9%83%A8%E7%BD%B2%E6%96%87%E6%A1%A3)
## 联系方式
版本更新及相关信息会在公众号及时同步。

在使用中有任何问题，欢迎反馈给我，可以用以下联系方式交流：

* shiwei138@163.com
* 公众号：大数据前线

![qr-code](https://github.com/NextMark/datashops/raw/master/docs/gzh_qrcode.jpg)
