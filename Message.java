import java.io.File;
import java.io.Serializable;

/**
 * Created by vikaasa on 11/15/2016.
 */
public class Message implements Serializable{

    enum SendType{
        UNICAST, BLOCKCAST, BROADCAST;
    }

    enum MessageType{
        TEXT, FILE, ADMIN;
    }

    private SendType sendType;
    private MessageType messageType;
    private String text;
    private File file;
    private String userName;
    private String senderUserName;
    byte[] fileContent;

    Message(SendType sendType, MessageType messageType){
        this.sendType = sendType;
        this.messageType = messageType;
    }

    public void setMessageType(MessageType messageType){
        this.messageType = messageType;
    }

    public MessageType getMessageType(){
        return this.messageType;
    }

    public void setSendType(SendType sendType){
        this.sendType = sendType;
    }

    public SendType getSendType(){
        return this.sendType;
    }

    public void setText(String text){
        this.text = text;
    }

    public String getText(){
        return text;
    }

    public void setFile(File file){
        this.file = file;
    }

    public File getFile(){
        return file;
    }

    public void setUserName(String userName){
        this.userName = userName;
    }

    public String getUserName(){
        return userName;
    }

    public void setSenderUserName(String userName){
        this.senderUserName = userName;
    }
    public String getSenderUserName(){
        return this.senderUserName;
    }

    public void setFileContent(byte[] bytes){
        this.fileContent = bytes;
    }

    public byte[] getFileContent(){
        return this.fileContent;
    }
}
