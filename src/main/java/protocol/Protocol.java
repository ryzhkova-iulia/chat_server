package protocol;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Юлия on 27.12.2015.
 */
public class Protocol {

    public static byte[] serialize(Message message) {
        /*byte[] data = new byte[4 + textMessageLength + 1];

        data[0] = len[0];
        data[1] = len[1];
        data[2] = len[2];
        data[3] = len[3];


        for (int i = 0; i < message.getNickName().getBytes().length; i++) {
            data[i+4] = message.getNickName().getBytes()[i];
        }

        for (int i = 0; i < message.getText().getBytes().length; i++) {
            data[i + 4 + message.getNickName().getBytes().length] = message.getText().getBytes()[i];
        }*/

        byte[] messageSerialized = serializeString(message.getText());
        byte[] nickSerialized = serializeString(message.getNickName());

        int packetLen = messageSerialized.length + nickSerialized.length;

        byte[] len = getBytes(packetLen);
        byte[] data = new byte[4 + packetLen + 1];

        data[0] = len[0];
        data[1] = len[1];
        data[2] = len[2];
        data[3] = len[3];

        System.arraycopy(nickSerialized, 0, data, 4, nickSerialized.length);
        System.arraycopy(messageSerialized, 0, data, 4 + nickSerialized.length, messageSerialized.length);



        data[data.length - 1] = getCrc(data, 4, data.length - 1);

        return data;
    }

    public static byte[] serializeString(String str) {
        byte[] strMass = new byte[4 + str.getBytes().length];
        byte[] length = getBytes(str.getBytes().length);

        strMass[0] = length[0];
        strMass[1] = length[1];
        strMass[2] = length[2];
        strMass[3] = length[3];

        System.arraycopy(str.getBytes(), 0, strMass, 4, str.getBytes().length);
        return  strMass;
    }


    public static byte[] getBytes(int i) {
        byte[] result = new byte[4];

        result[0] = (byte) (i >> 24);
        result[1] = (byte) (i >> 16);
        result[2] = (byte) (i >> 8);
        result[3] = (byte) (i /*>> 0*/);

        return result;
    }

    public static byte getCrc(byte[] data, int start, int stop) {
        byte crc = 0;
        for (int i = start; i < stop; i++) {
            crc = (byte) (crc + data[i]);
        }
        return crc;
    }

    public static Message deserialize(InputStream input) throws IOException {
        byte[] length = new byte[4];
        input.read(length);
        byte[] payLoad = new byte[Protocol.byteToInt(length)];
        input.read(payLoad);


        byte[] crc = new byte[1];
        input.read(crc);

        return deserialize(payLoad);
    }


    public static Message deserialize(byte[] payLoad) {
        Message msg = new Message();


        int nickSerializedLeng = bytesToInt(payLoad[0], payLoad[1], payLoad[2], payLoad[3]);
        byte[] nickSerializedData = new byte[nickSerializedLeng];

        System.arraycopy(payLoad, 4, nickSerializedData, 0, nickSerializedLeng);

        int messageSerializedLen = bytesToInt(payLoad[4 + nickSerializedLeng],
                payLoad[4 + nickSerializedLeng + 1],
                payLoad[4 + nickSerializedLeng + 2],
                payLoad[4 + nickSerializedLeng + 3]);

        byte[] messageSerializedData = new byte[messageSerializedLen];

        System.arraycopy(payLoad, 4 + 4 + nickSerializedLeng, messageSerializedData, 0, messageSerializedLen );

        msg.setText(new String(messageSerializedData));
        msg.setNickName(new String(nickSerializedData));

        return msg;
    }

    public static int byteToInt (byte[] mass) {
        return mass[3] & 0xFF |
                (mass[2] & 0xFF) << 8 |
                (mass[1] & 0xFF) << 16 |
                (mass[0] & 0xFF) << 24;
    }

    public static int bytesToInt (byte byte0, byte byte1, byte byte2, byte byte3) {
        return byte3 & 0xFF |
                (byte2 & 0xFF) << 8 |
                (byte1 & 0xFF) << 16 |
                (byte0 & 0xFF) << 24;
    }

}
