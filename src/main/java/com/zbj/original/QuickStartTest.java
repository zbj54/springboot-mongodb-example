package com.zbj.original;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.inc;

public class QuickStartTest {


    MongoClient mongoClient = new MongoClient("localhost" , 27017);
    //或者使用下面的方法，使用连接字符串进行构建Mongo客户端实例
//	 MongoClientURI connectionString = new MongoClientURI("mongodb://localhost:27017");
//	 MongoClient mongoClient = new MongoClient(connectionString);

    /**
     * 一旦有了连接到MongoDB部署的MongoClient实例，就可以使用MongoClient.getdatabase()方法来访问数据库。
     * 将数据库的名称指定到getDatabase()方法。如果数据库不存在，MongoDB将在您第一次为该数据库存储数据时创建数据库
     * 数据库实例不可变
     */
    MongoDatabase database = mongoClient.getDatabase("zbj");

    /**
     *
     * 一旦有了DB实例后，就使用它的getCollection()方法来访问一个集合。
     * 将集合的名称指定到getCollection()方法。如果一个集合不存在，MongoDB将在您第一次为该集合存储数据时创建集合。
     */

    /**
     * 例如，使用数据库实例，下面的语句访问mydb数据库中的名为test的集合
     * 集合不可变
     */
    MongoCollection<Document> collection = database.getCollection("users");

    /**
     * 存入一条数据【插入一个文档对象】
     */

    @Test
    public void SaveOne(){


        /**
         * 有了DB实例、Collection集合后，就该存储文档（数据）了
         * 要使用Java驱动程序创建文档，请使用Document(文档)类 --org.bson.Document
         * 要使用Java驱动程序创建文档，实例化一个具有字段和值的文档对象
         * new Document(Map<String,Object>) 或者  new Document(key,value)
         * 并使用其append()方法将其他字段和值包含到文档对象中
         * 该值可以是另一个文档对象，以指定嵌入的文档
         */

        /**
         * 数据（含文档套文档） :
         {
         "name" : "MongoDB",
         "type" : "nosqlDB",
         "count" : 1,
         "versions": [ "v3.6", "v3.0", "v2.6" ],
         "info" : { x : 203, y : 102 }
         }
         */
        Map<String, Object> map = new HashMap<>();
        map.put("x", 203);
        map.put("y", 102);
        Document document = new Document("name","MongoDB")
                .append("type", "nosqlDB")
                .append("count", 1)
                .append("versions", Arrays.asList("v3.6","v3.0","v2.6"))
                .append("info", new Document(map));

        /**
         * 有了文档对象后，就是向集合collection中保存了
         * 也可以批量插入List<Document>，方法为collection.insertMany(documents);
         */

        collection.insertOne(document);

        /**
         * 要计算集合中的文档数，可以使用集合的count()方法。
         */

        System.err.println("count of documents from collection is :"+collection.count());

    }

    /**
     * 无条件取出（查询）第一个文档对象（第一条数据）
     */
    @Test
    public void findOne(){

        Document doc = collection.find().first();
        System.err.println("只取【test】集合中的第一个文档对象："+doc.toJson());
    }

    /**
     * 无条件取出（查询）所有的文档对象（所有数据）
     */
    @Test
    public void findAll(){

        /**
         * 要检索集合中的所有文档，我们将使用find()方法，而不需要任何参数。
         * 为了遍历结果，将iterator()方法链接到find()中。
         * 迭代器类似SQL的游标cursor
         */
        MongoCursor<Document> cursor = collection.find().iterator();
        try {
            /**
             * 如果迭代到下一个对象等于false，退出迭代，并关闭迭代器
             */
            int index = 0;
            while (cursor.hasNext()) {
                System.err.println((index+1)+":"+cursor.next().toJson());
                index++;
            }
        } finally {
            cursor.close();
        }

        System.err.println("=======不推荐下面的遍历法");
        /**
         * 虽然可以允许迭代使用下面的习惯用法，但避免使用它，因为如果循环提前终止，应用程序可能会泄漏一个游标:
         */
        for (Document cur : collection.find()) {
            System.err.println(cur.toJson());
        }
    }


    /**
     * 带条件查询文档对象
     */
    @Test
    public void queryOne(){

        /**
         * 要查询匹配某些条件的文档，请将filter对象传递给find()方法。
         * 为了方便创建过滤器对象，Mongo-Java驱动程序提供了过滤器帮助器。
         */

        /**
         * 查询count == 1 的文档对象
         */
        Document doc = collection.find(eq("count", 1)).first();
        if(doc!=null){
            System.err.println("条件1查询结果："+doc.toJson());
        }else{
            System.err.println("条件1查询结果：无");
        }


        /**
         * 查询count<2  且 name.equals("Mongo")的文档对象,显然不存在
         * 如果查询全部符合条件的文档集合，请使用find(*).iterator()
         */
        doc = collection.find(and(lt("count",2),eq("name", "Mongo"))).first();
        if(doc!=null){
            System.err.println("条件2查询结果："+doc.toJson());
        }else{
            System.err.println("条件2查询结果：无");
        }

        /**
         * Filters
         * gt gte  === >  >=
         * lt lte  === <  <=
         * eq      === ==
         * and     === 相当于 sql语句的 where xxx  and xxx
         * or      === 相当于 sql语句的 where xxx  or  xxx
         */

    }

    /**
     * 更新文档 -- ByOne
     */
    @Test
    public void updateOne(){

        /**
         * 要更新集合中的文档，使用集合的updateOne方法更新单个文档对象
         *
         * $set  相当于 sql语句的 update collection set type = "database" where name = "MongoDB" limit 1
         */
        UpdateResult result = collection.updateOne(eq("name", "MongoDB"), new Document("$set", new Document("type", "database")));
        System.err.println("更新影响的行数："+result.getModifiedCount());

    }

    /**
     * 批量更新文档 -- ByMany
     */
    @Test
    public void updateMany(){

        /**
         * 要更新集合中的文档，使用集合的updateMany方法更新所有符合条件的文档对象
         * 下面更新所有name等于"MongoDB"的文档对象的count属性 +100
         */
        UpdateResult result = collection.updateMany(eq("name", "MongoDB"), inc("count", 100));
        System.err.println("更新影响的行数："+result.getModifiedCount());

    }


    /**
     * 删除文档 -- ByOne
     */
    @Test
    public void deleteOne(){
        /**
         * 删除第一个type.equals("nosqlDB")的文档对象
         */
        DeleteResult result = collection.deleteOne(eq("type", "nosqlDB"));
        System.err.println("删除影响的行数："+result.getDeletedCount());
    }

    /**
     * 批量删除文档 -- ByMany
     */
    @Test
    public void deleteMany(){

        /**
         * 删除所有count>=100的文档对象
         */
        DeleteResult result = collection.deleteMany(gte("count", 100));
        System.err.println("删除影响的行数："+result.getDeletedCount());
    }

}
