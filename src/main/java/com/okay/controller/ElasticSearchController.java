package com.okay.controller;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.hibernate.validator.constraints.pl.REGON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class ElasticSearchController {

    @Autowired
    private TransportClient client;

    /**
     * 根据id查询book索引novel类型的数据
     * @param id 查询_id值的数据
     * @return
     */
    @RequestMapping("/get/book/novel")
    @ResponseBody
    public ResponseEntity get(@RequestParam(name = "id", defaultValue = "") String id){

        if (id.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        // book为索引，novel为类型，id为book索引中的_id值
        GetResponse result = this.client.prepareGet("book", "novel", id).get();
        if (!result.isExists()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(result.getSource(), HttpStatus.OK);
    }

    /**
     * 添加数据
     * @param id
     * @param author
     * @param name
     * @param price
     * @param date
     * @return
     */
    @RequestMapping("/book/novel")
    @ResponseBody
    public ResponseEntity add(@RequestParam("id") String id,
                              @RequestParam("author") String author,
                              @RequestParam("name") String name,
                              @RequestParam("price") int price,
                              @RequestParam("date")
                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date date) {
        try {
            // 构造数据
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
}