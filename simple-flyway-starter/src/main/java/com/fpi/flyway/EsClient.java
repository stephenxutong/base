package com.fpi.flyway;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.google.common.collect.Lists;

public class EsClient {

	private static final String ClusterName = "rd-es";
	private static final String GatewayIpPorts = "111.231.207.232:9300";

	private static TransportClient client;

	public static TransportClient client() throws UnknownHostException {
		Settings settings = Settings.builder().put("cluster.name", ClusterName).put("client.transport.sniff", false)
				.put("client.transport.ping_timeout", "10s").put("transport.type", "netty4").put("http.type", "netty4")
				.build();

		String[] oneInstance = GatewayIpPorts.split(",");
		for (String item : oneInstance) {
			String[] ipPort = item.split(":");
			if (null == client) {
				client = new PreBuiltTransportClient(settings);
			}
			try {
				client.addTransportAddress(
						new InetSocketTransportAddress(InetAddress.getByName(ipPort[0]), Integer.parseInt(ipPort[1])));
			} catch (UnknownHostException e) {
				System.err.println("unknown host for transport client, ip:{}, port:{}" + ipPort[0] + ipPort[1]);
			}
		}

		for (String item : oneInstance) {
			String[] ipPort = item.split(":");
			client.addTransportAddresses(
					new InetSocketTransportAddress(InetAddress.getByName(ipPort[0]), Integer.parseInt(ipPort[1])));
		}

		return client;
	}

	public static byte[] toUtf8Bytes(char[] chars) {
		CharBuffer charBuffer = CharBuffer.wrap(chars);
		ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(charBuffer);
		byte[] bytes = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
		Arrays.fill(byteBuffer.array(), (byte) 0);
		return bytes;
	}

	public static void test(String[] args) throws UnknownHostException {
		TransportClient client = EsClient.client();
		System.out.println(client);
		SearchRequestBuilder requestBuilder = client.prepareSearch();
		requestBuilder.setIndices("ept_goods");
		requestBuilder.setQuery(QueryBuilders.boolQuery().must(new TermQueryBuilder("content", "我是个好人")));
		MatchQueryBuilder matchQry = new MatchQueryBuilder("content", "我是个好人");
		requestBuilder.setQuery(matchQry);
		System.out.println(requestBuilder.toString());
		SearchResponse response = requestBuilder.execute().actionGet();
		System.out.println(response.toString());
	}
	
	public static void main(String[] args) {
		String kk = "7-9-10-5-8-4-2-1-6-3-7-9-10-5-8-4-2";
		List<Integer> scaleList = Lists.newArrayList();
		Arrays.asList(kk.split("-")).forEach(t->{
			scaleList.add(Integer.parseInt(t));
		});
		String id = "37072519881105149";
		List<Integer> idNums = Lists.newArrayList();
		for(char c : id.toCharArray()) {
			idNums.add(Integer.parseInt(String.valueOf(c)));
		}
		
		int i = 0;
		int sum = 0;
		for(Integer e: idNums) {
			sum += e * scaleList.get(i++);
		}
		String map = "1-0-X-9-8-7-6-5-4-3-2";
		String[] keys = map.split("-");
		for(int m=0; m<keys.length; m++) {
			if(m == sum%11) {
				System.out.println(keys[m]);
			}
		}
	}
}