package offstage.crypt;

/*
 Public Key cryptography using the RSA algorithm.
*/

 
	

import java.security.interfaces.*;
import javax.crypto.*;
import java.io.*;
import java.security.spec.*;
import java.security.*;
import java.io.*;

public class Crypt {

static String readFile(File f) throws IOException
{
	BufferedReader in = new BufferedReader(new FileReader(f));
	for (;;) {
		String s = in.readLine();
		if (s.startsWith("-----")) break;
	}
	
	StringBuffer sb = new StringBuffer();
	for (;;) {
		String s = in.readLine();
		if (s.startsWith("-----")) break;
		sb.append(s);
	}
	
	in.close();
	return sb.toString();
	
}
	
public static void main (String[] args) throws Exception
{
//	File keyFile = new File("/export/home/citibob/tmp");
	File keyFile = new File("/Users/citibob/tmp");
	KeyFactory keyFactory = KeyFactory.getInstance("RSA");

	// read public key DER file
	
	byte[] bytes = pubdomain.Base64.decode(readFile(
		new File(keyFile, "public.txt")));

	// decode public key
	X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(bytes);
	PublicKey pubKey = (RSAPublicKey) keyFactory.generatePublic(pubSpec);

	// Use public key to encrypt a message
	Cipher ecipher = Cipher.getInstance("RSA");
	ecipher.init(Cipher.ENCRYPT_MODE, pubKey);
	
	String plainString = "Hello World!  How are you today?";
plainString = plainString + plainString + plainString + plainString + plainString;
	byte[] plainText = plainString.getBytes("UTF8");
	byte[] cipherText = ecipher.doFinal(plainText);
	
	System.out.println("cipher text:");
	System.out.println(pubdomain.Base64.encodeBytes(cipherText));

	
	
	// decode private key
	bytes = pubdomain.Base64.decode(readFile(
		new File(keyFile, "private.txt")));
	PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(bytes);
	PrivateKey privKey = (RSAPrivateKey) keyFactory.generatePrivate(privSpec);

	Cipher decipher = Cipher.getInstance("RSA");
	decipher.init(Cipher.DECRYPT_MODE, privKey);
	byte[] decodeText = decipher.doFinal(cipherText);
	String cipherString = new String(decodeText, "UTF8");
	
	System.out.println("decoded: " + cipherString);
}
}