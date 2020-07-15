package com.yc.flume;

import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.OperatingSystem;
import nl.bitwalker.useragentutils.UserAgent;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*  待分解的数据格式:
    %h %l %u %t %s %b %D \"%r\" \"%{i,Referer}\" \"%{i,User-Agent}\

   %h: 远程主机名
   %l:  远程主机逻辑名: 经常是 -
   %u:   远程受信用户名: 经常是 -
   %t: 日期和时间，用 Common Log Format格式
   %r: Http请求的第一行
   %s: 响应状态码
   %b: 发送的字节数（不包括头域),如是 - 表明没有字节发送，
   "%{i,Referer}\":   从http请求头中获取 来源地址
   %{i,User-Agent}:   从http请求头中获取 userAgent

   注意：这一条日志正好是一行，经查一行大约在  2048个字符以内.

  192.168.0.106 - - 200 3220 199 [11/Jul/2020:11:25:33 +0800] "POST /yc74ibike/findNearAll HTTP/1.1"  "https://servicewechat.com/wx56d05b16436e1eeb/devtools/page-frame.html" "Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_1 like Mac OS X) AppleWebKit/603.1.3 (KHTML, like Gecko) Version/10.0 Mobile/14E304 Safari/602.1 wechatdevtools/1.03.2005140 MicroMessenger/7.0.4 Language/zh_CN webview/"

 */
public class AccessLogInterceptor implements Interceptor {
    private Logger logger = Logger.getLogger(AccessLogInterceptor.class.getName());

    @Override
    public void initialize() {
    }

    @Override
    public Event intercept(Event event) {
        //注意这里 一个  event是一天的日志中的一行记录，要对它进行解析.
        String line = new String(event.getBody());
        String[] fields = line.split(" ");
        if (fields == null || fields.length <= 0) {
            return null;
        }
        String remoteIp = fields[0];
        String loginRemoteName = fields[1];
        String authrizedName = fields[2];
        String responseCode = fields[3];
        String contentBytes = fields[4];
        String handleTime = fields[5];

        Pattern p = Pattern.compile("((.* \\[)?([a-zA-Z0-9: +/]*)?(\\] .*)?)");
        Matcher m = p.matcher(line);
        m.find();
        String time = m.group(3);    // 11/Jul/2020:11:25:33 +0800
        //转换成时间搓
        SimpleDateFormat format = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss z", Locale.ENGLISH);
        long timestamp = 0;
        try {
            timestamp = format.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        p = Pattern.compile("((.* \")?(.*)?\" \"(.*)?\" \"(.*)?\")");  //
        m = p.matcher(line);
        m.find();
        String requestUrl = m.group(3);
        String refer = m.group(4);
        String browserinfo = m.group(5);
        //处理请求信息
        String[] requestParams = requestUrl.split(" ");  //由一个部分组成    [请求方式, 地址, 协议]
        if (requestParams[1].split("\\?").length > 0) {
            requestParams[1] = requestParams[1].split("\\?")[0];   //只保留了请求地址
        }
        System.out.println(browserinfo+"....");
        //转成UserAgent对象
        UserAgent userAgent = UserAgent.parseUserAgentString(browserinfo);
        //获取浏览器信息
        Browser browser = userAgent.getBrowser();

        //获取系统信息
        OperatingSystem os = userAgent.getOperatingSystem();
        //系统名称
        String system = os.getName();
        //浏览器名称
        String browserName = browser.getName();

        StringBuffer sb=new StringBuffer();
        sb.append(remoteIp+"\t"+loginRemoteName+"\t"+authrizedName+"\t"+responseCode+"\t"+contentBytes+"\t"+handleTime+"\t"
                +timestamp+"\t"+requestParams[0]+"\t"+requestParams[1]+"\t"+requestParams[2]+"\t"+refer+"\t"+system+"\t"+browserName);
        event.setBody(   sb.toString().getBytes() );
        return event;
    }

    @Override
    public List<Event> intercept(List<Event> events) {
        for (Event e : events) {
            intercept(e);
        }
        return events;
    }

    @Override
    public void close() {

    }

    /**
     * Interceptor.Builder的生命周期方法
     * 构造器 -> configure -> build
     */
    public static class Builder implements Interceptor.Builder {
        @Override
        public Interceptor build() {
            //在build创建AccessLogInterceptor的实例
            return new AccessLogInterceptor();
        }

        @Override
        public void configure(Context context) {
            //暂时没有参数
        }
    }
}
