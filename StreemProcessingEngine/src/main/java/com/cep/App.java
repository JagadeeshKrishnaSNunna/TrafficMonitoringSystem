package com.cep;
import com.cep.Connector.natsConnector;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;
public class App
{
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> dataStream = env.addSource(new natsConnector());
        DataStream<JsonObject> parse=dataStream.flatMap(new parser());
        DataStream<JsonObject> search=parse.flatMap(new searcher());
        DataStream<String> notify=search.flatMap((new Notify()));
        search.print();
        env.execute();
    }
    public static class parser implements FlatMapFunction<String, JsonObject>{

        @Override
        public void flatMap(String value, Collector<JsonObject> out) throws Exception {
//            out.collect(new Gson().fromJson(value,JsonObject.class));
            out.collect(new JsonParser().parse(value).getAsJsonObject());
        }
    }

    public static class searcher implements FlatMapFunction<JsonObject, JsonObject>{

        @Override
        public void flatMap(JsonObject value, Collector<JsonObject> out) throws Exception {
            final String url= "https://my-deployment-315ae5.es.us-central1.gcp.cloud.es.io:9243/criminalrecords/_search?q=license:"+value.get("license");
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor("elastic","<KEY>"));
            String result = restTemplate.getForObject(url, String.class);
            JsonObject json= (new JsonParser().parse(result).getAsJsonObject());
            String st=json.get("hits").getAsJsonObject().get("total").getAsJsonObject().get("value").toString();
            if(st.compareTo("1")==0){
                out.collect(value);
            }
        }
    }
    public static class Notify implements FlatMapFunction<JsonObject,String>{

        @Override
        public void flatMap(JsonObject value, Collector<String> out) throws Exception {
            String data="Vehicle with License registration "+value.get("license")+" found at location "+value.get("location")+" on "+new java.util.Date();
            final String mailUrl="http://localhost:8080/notify?data="+data;
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(mailUrl, String.class);
                out.collect(result);
        }
    }
}
