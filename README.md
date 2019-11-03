# ElasticSearch 7.4
7.4与5.5-6.8的主要区别在于建立索引是是否需要指定类型等，前者不需要（默认使用_doc作为类型），后者需要
## 一、基础操作
启动elasticSearch-head命令：
```bat
npm run start
```
访问`localhost:9100`即可访问到elasticSearch-head

启动elasticSearch，window直接双击bin目录下的`elasticsearch.bat`既可以
1. > ##### 使用postman建立文档索引

 请求地址：`http:127.0.0.1:9200/people`
 请求参数：
 ```json
 {
	"settings":{
		"number_of_shards":3, --指定当前索引的分片数
		"number_of_replicas":1 --指定当前索引的备份数
	},
	"mappings":{ --指定索引的数据映射定义
		"properties":{ --指定索引的属性定义
			"type":{"type":"keyword"},
			"name":{"type":"text"},
			"country":{"type":"keyword"},
			"age":{"type":"integer"},
			"date":{
				"type": "date",
				"format":"yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis"
			}
		}
	}
 }
 ```
如果_mapping为空{}则为非结构化索引，如果建立结构化索引可以使用以下方式建立：
在elasticsearch-head的复合查询中或postman中请求
`http://localhost:9200/book/_mappings`(http://ip:port:index_name/_mapping)
参数：
```json 
{
  "properties": {
    "title": {
      "type": "text"
    }
  }
}
```
---
2. >#### 插入数据

- 非指定文档id插入数据

请求地址：`http://127.0.0.1:9200/people/_doc/` post方式请求

请求参数：
```json
{
	"name":"重瓦力",
	"country": "China",
	"age": 30,
	"date": "2019-11-02"
}
```
- 指定文档id插入数据

请求地址：`http://127.0.0.1:9200/people/_doc/1` put方式请求

请求参数：
```json
{
	"name":"瓦力",
	"country": "China",
	"age": 30,
	"date": "2019-11-02"
}
```
---
3. >#### 修改文档

- 指定id修改文档
请求地址：`http://127.0.0.1:9200/people/_doc/1/_update` （post方式请求，1为数据_id值，_update为指定操作）

参数：
```json
{
	"doc":{"name":"谁是瓦力"} -- doc为关键字修改文本的name属性
}
```
- 通过脚本方式修改
请求地址：`http://127.0.0.1:9200/people/_doc/1/_update` （post方式）

参数：
```json
{
	"script":{"lang": "painless", --指定脚本语言
		"inline": "ctx._source.age+=params.age",  --指定脚本内容
		"params": {     -- 参数值
			"age":100
		}
	}
}
```
---
3. >#### 删除操作

- 通过id删除数据

 请求地址 `http://127.0.0.1:9200/people/_doc/1` （删除数据id为1的数据，请求方式delete请求）

- 删除索引

1. 可以通过head插件进行删除
1. 使用postman进行删除

请求地址：`http://127.0.0.1:9200/people` （**删除操作非常危险，谨慎使用**）

---
4. >#### 查询操作
- 指定数据id查询

请求地址： `http://127.0.0.1:9200/people/_doc/1` (查询id为1的people索引文档数据)

返回值：
```json
{
	"_index": "people",
	"_type": "_doc",
	"_id": "1",
	"_version": 1,
	"_seq_no": 6,
	"_primary_term": 1,
	"found": true,
	"_source": {
		"name": "重瓦力",
		"country": "China",
		"age": 30,
		"date": "2019-11-02"
	}
}
```

- 条件查询
请求地址`http://127.0.0.1:9200/people/_search` (_search为内置方法操作，get方式请求)

请求参数：**查询全部数据**
```json
{
	"query":{
		"match_all":{} --代表查询所有
	},
	"from":1,  --从哪里开始查询（可忽略）
	"size":1   --查询多少条数据（可忽略）
}
```
返回结果：
```json
{
	"took": 5,  --总耗时
	"timed_out": false,
	"_shards": {
		"total": 3,
		"successful": 3,
		"skipped": 0,
		"failed": 0
	},
	"hits": {
		"total": {
			"value": 5,
			"relation": "eq"
		},
		"max_score": 1.0,
		"hits": [
			{
				"_index": "people",
				"_type": "_doc",
				"_id": "bUqBKm4BaA3LSbA4MgPv",
				"_score": 1.0,
				"_source": {
					"name": "重瓦力",
					"country": "China",
					"age": 30,
					"date": "2019-11-02"
				}
			}
		]
	}
}
```
 **<p id="query1"> 指定条件查询 </p>**
请求参数：
```json
{
	"query":{  --更多详细用法可查阅资料
		"match":{   -- 指定条件查询匹配，可以匹配到条件中任何一个词；(其它关键字还有match_phrase为全词匹配；multi_match为多字段匹配；query_string为语法查询，range可对某个字段做范围查询，更多详细用法可以查阅资料)
            "name":"22"
        }
    },
    "sort": [     --排序（可忽略）
		{"age": {"order":"desc"}}	--根据age降序排序
	],
	"from":1,  --从哪里开始查询（可忽略）
	"size":1   --查询多少条数据（可忽略）
}
```
返回结果：
```json
{
	"took": 125,
	"timed_out": false,
	"_shards": {
		"total": 3,
		"successful": 3,
		"skipped": 0,
		"failed": 0
	},
	"hits": {
		"total": {
			"value": 1,
			"relation": "eq"
		},
		"max_score": 0.2876821,
		"hits": [
			{
				"_index": "people",
				"_type": "_doc",
				"_id": "b0quKm4BaA3LSbA4-gNa",
				"_score": 0.2876821,
				"_source": {
					"name": "重瓦力22",
					"country": "China",
					"age": 30,
					"date": "2019-11-02"
				}
			}
		]
	}
}
```
请求参数：**聚合查询**
```json
{
	"aggs":{     --aggs为聚合查询关键词
		"group_by_age":{  --自定义聚合名称（可以定义多个作为复合聚合）
			"terms": {  --聚合关键词根据字段查询，（其他关键字包含有stats对某个field进行结果计算总数等）
				"field":"age"
			}
		}
	}
}
```
返回结果：
```json
{
	"took": 3,
	"timed_out": false,
	"_shards": {
		"total": 3,
		"successful": 3,
		"skipped": 0,
		"failed": 0
	},
	"hits": {
		"total": {
			"value": 6,
			"relation": "eq"
		},
		"max_score": 1.0,
		"hits": [
			{
				"_index": "people",
				"_type": "_doc",
				"_id": "b0quKm4BaA3LSbA4-gNa",
				"_score": 1.0,
				"_source": {
					"name": "重瓦力22",
					"country": "China",
					"age": 30,
					"date": "2019-11-02"
				}
            },
            ...
		]
	},
	"aggregations": {
		"group_by_age": {   --聚合信息
			"doc_count_error_upper_bound": 0,
			"sum_other_doc_count": 0,
			"buckets": [
				{
					"key": 30,   --age=30的有4个
					"doc_count": 4
				},
				{
					"key": 10,
					"doc_count": 1
				},
				{
					"key": 20,
					"doc_count": 1
				}
			]
		}
	}
}
```
---
## 二、高级查询
高级查询分为
- <a href="#query1">子条件查询 ： 特定字段查询所指特定值query</a>

filter用法参数
```json
{
    "query":{
        "bool":{  --filter需要结合bool使用
            "filter":{
                "term": {
                    "age":20   --过滤条件
                }
            }
        }
    }
}
```

Query Context : 在查询过程中，除了判断文档是否满足查询条件外，ES还会计算一个_score来标识匹配的程度，旨在判断目标文档和查询条件匹配的有多好

常用查询
1. 全文本查询：针对文本类型数据
1. 字段级别查询： 针对结构化数据，如数字，日期等

- 复合条件查询：以一定的逻辑组合子条件查询

指定分数用法：
固定分数查询不支持match，只支持filter
```json
{
    "query":{
        "constant_score":{  --固定分数查询
            "filter":{
                "match":{
                    "age":10
                }
            },
            "boost":2
        }
    }
}
```
should关键字用法
```json
{
    "query":{
        "bool":{
            "should":[  --只需满足以下两个条件中的其中一个即可（其它关键字must为必须满足；must_not必须不能满足）
                {
                    "match":{
                        "author":"测试okay"
                    }
                },
                {
                    "match":{
                        "title":"ElasticSearch语法"
                    }
                }
            ]
        }
    }
}
```

常用查询
- 固定分数查询
- 布尔查询
- ...more

## 二、整合springboot
注意：**使用版本和上面ElasticSearch7.4版本不同，下面是从新使用5.5版本进行整合以及演示**
1. > #### pom文件添加依赖
```xml
 <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.9.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.okay</groupId>
    <artifactId>sping-boot-es</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>sping-boot-es</name>
    <description>Demo project for Spring Boot</description>

    <properties>
        <java.version>1.8</java.version>
        <elasticsearch.version>5.5.2</elasticsearch.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!-- https://mvnrepository.com/artifact/org.elasticsearch.client/transport -->
        <dependency>
            <groupId>org.elasticsearch.client</groupId>
            <artifactId>transport</artifactId>
            <version>${elasticsearch.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-core</artifactId>
            <version>2.7</version>
        </dependency>

    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
```
2. > #### 编写测试例子
添加配置类MyConf.java
```java
@Configuration
public class MyConf {

    @Bean
    public TransportClient client() throws UnknownHostException {

        // 这里使用的tcp端口为9300，默认
        InetSocketTransportAddress  node = new InetSocketTransportAddress(
                InetAddress.getByName("localhost"),
                9300
        );

        Settings settings = Settings.builder()
                .put("cluster.name","byterun-es")
                .build();

        TransportClient client = new PreBuiltTransportClient(settings);
        client.addTransportAddress(node);

        return client;
    }
}
```
- 添加log4j2.properties到springboot的资源目录下，内容：
```xml
appender.console.type=Console
appender.console.name=console
appender.console.layout.type= PatternLayout
appender.console.layout.pattern=[%t] %-5p %c - %m%n

rootLogger.level=info
rootLogger.appenderRef.console.ref=console
```
- 添加controller请求es查询数据
```java

@RestController
public class ElasticSearchController {

    @Autowired
    private TransportClient client;

    /**
	* id为es里存在的_id值
	*/
    @RequestMapping("/get/book/novel")
    @ResponseBody
    public ResponseEntity get(@RequestParam(name = "id", defaultValue = "") String id){

        if (id.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
	    // book为索引，novel为类型（5.5-6.8版本的es含有）
        GetResponse result = this.client.prepareGet("book", "novel", id).get();
        if (!result.isExists()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(result.getSource(), HttpStatus.OK);
    }

}
```
- 添加增加数据接口
```java
 @RequestMapping("/book/novel")
    @ResponseBody
    public ResponseEntity add(@RequestParam("id") String id,
                              @RequestParam("author") String author,
                              @RequestParam("name") String name,
                              @RequestParam("price") int price,
                              @RequestParam("date")
                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date date) {
        try {
            XContentBuilder xContentBuilder = XContentFactory.jsonBuilder()
                    .startObject()
                    .field("id", id)
                    .field("author", author)
                    .field("name", name)
                    .field("price", price)
                    .field("date", date.getTime())
                    .endObject();
            IndexResponse result = this.client.prepareIndex("book", "novel")
                    .setSource(xContentBuilder).get();

            return new ResponseEntity(result.getId(),HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
```
启动springboot运用即可访问。
