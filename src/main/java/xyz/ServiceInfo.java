package xyz;

import io.vertx.core.json.JsonObject;

import java.net.Socket;

/**
 * Created by Ravi on 6/19/2015.
 */
public class ServiceInfo implements Comparable<ServiceInfo> {
    String name="";
    String nodeId="";

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    private Socket socket;

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    String path="";
    int port;
    public ServiceInfo(){}
    public ServiceInfo(JsonObject jsonObject){
        if(jsonObject!=null){
            name= jsonObject.getString("name");
            port= jsonObject.getInteger("port");
            host= jsonObject.getString("host");
            metaInfo= jsonObject.getString("metaInfo");
            path= jsonObject.getString("path");
            nodeId= jsonObject.getString("nodeId");
        }
    }
    public String getName() {
        return name==null?"":name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getMetaInfo() {
        return metaInfo;
    }

    public void setMetaInfo(String metaInfo) {
        this.metaInfo = metaInfo;
    }

    String host="localhost";
    String metaInfo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceInfo)) return false;

        ServiceInfo that = (ServiceInfo) o;

        if (getPort() != that.getPort()) return false;
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
        if (!getPath().equals(that.getPath())) return false;
        return getHost().equals(that.getHost());

    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + getPath().hashCode();
        result = 31 * result + getPort();
        result = 31 * result + getHost().hashCode();
        return result;
    }

    public JsonObject toJson(){
        JsonObject object=new JsonObject();
        object.put("port",port);
        object.put("name",name);
        object.put("path",path);
        object.put("host",host);
        object.put("nodeId",nodeId);
        return object;
    }

    @Override
    public int compareTo(ServiceInfo info) {
        return info.getPath().compareTo(path);
    }
}
