package be.masi_liege_g2_2223;

import com.sun.javacard.apduio.Apdu;
import com.sun.javacard.apduio.CadT1Client;
import com.sun.javacard.apduio.CadTransportException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class JCManager {
	
	/******************** Constantes ************************/ 
	public static final byte CLA_PROJECTAPPLET= (byte) 0xB0; 
	public static final byte INS_GET_POINTS= 0x00; 
	public static final byte INS_SET_POINTS= 0x01; 
	public static final byte INS_RES_POINTS= 0x02;
	public static final byte INS_GET_ID= 0x03; 
	public static final byte INS_SET_ID= 0x04; 
	public static final byte INS_RES_ID= 0x05;
	public static final byte INS_RES_CARD= 0x06;
	
	private CadT1Client cad;

	public JCManager(Socket socket) throws IOException {
		BufferedInputStream input = new BufferedInputStream(socket.getInputStream());
        BufferedOutputStream output = new BufferedOutputStream(socket.getOutputStream());
        cad = new CadT1Client(input, output);
	}

	public void connect() throws IOException, CadTransportException {
		cad.powerUp();

        Apdu apdu = new Apdu();
        apdu.command[Apdu.CLA] = 0x00;
        apdu.command[Apdu.INS] = (byte) 0xA4;
        apdu.command[Apdu.P1] = 0x04;
        apdu.command[Apdu.P2] = 0x00;
        byte[] appletAID = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x00, 0x00 };
        apdu.setDataIn(appletAID);
        cad.exchangeApdu(apdu);

        if (apdu.getStatus() != 0x9000) {
            System.out.println("Erreur lors de la s�lection de l'applet");
            System.exit(1);
        }
	}

	public void disconnect() throws IOException, CadTransportException {
		cad.powerDown();
	}

	public int getPoints() throws IOException, CadTransportException {
		Apdu apdu = new Apdu();
        apdu.command[Apdu.CLA] = CLA_PROJECTAPPLET;
        apdu.command[Apdu.INS] = INS_GET_POINTS;
        cad.exchangeApdu(apdu);

        if (apdu.getStatus() != 0x9000) {
            System.out.println("Erreur : status word different de 0x9000");
            return -1;
        } else {
            return apdu.dataOut[0];
        }
	}

	public void setPoints(int points) throws IOException, CadTransportException {
		Apdu apdu = new Apdu();
        apdu.command[Apdu.CLA] = CLA_PROJECTAPPLET;
        apdu.command[Apdu.INS] = INS_SET_POINTS;
        byte[] data = { (byte) points };
        apdu.setDataIn(data);
        cad.exchangeApdu(apdu);
	}
	
	public int getId() throws IOException, CadTransportException {
		Apdu apdu = new Apdu();
        apdu.command[Apdu.CLA] = CLA_PROJECTAPPLET;
        apdu.command[Apdu.INS] = INS_GET_ID;
        cad.exchangeApdu(apdu);

        if (apdu.getStatus() != 0x9000) {
            System.out.println("Erreur : status word different de 0x9000");
            return -1;
        } else {
            return apdu.dataOut[0];
        }
	}

	public void setId(int id) throws IOException, CadTransportException {
		Apdu apdu = new Apdu();
        apdu.command[Apdu.CLA] = CLA_PROJECTAPPLET;
        apdu.command[Apdu.INS] = INS_SET_ID;
        byte[] data = { (byte) id };
        apdu.setDataIn(data);
        cad.exchangeApdu(apdu);
	}
	
	public void resetPoints() throws IOException, CadTransportException {
		Apdu apdu = new Apdu();
        apdu.command[Apdu.CLA] = CLA_PROJECTAPPLET;
        apdu.command[Apdu.INS] = INS_RES_POINTS;
        cad.exchangeApdu(apdu);
        
        if (apdu.getStatus() != 0x9000) {
            System.out.println("Erreur : status word different de 0x9000");
        } else {
            System.out.println("OK");
        }
	}
	
	public void resetId() throws IOException, CadTransportException {
		Apdu apdu = new Apdu();
        apdu.command[Apdu.CLA] = CLA_PROJECTAPPLET;
        apdu.command[Apdu.INS] = INS_RES_ID;
        cad.exchangeApdu(apdu);
        
        if (apdu.getStatus() != 0x9000) {
            System.out.println("Erreur : status word different de 0x9000");
        } else {
            System.out.println("OK");
        }
	}
	
	public void resetCard() throws IOException, CadTransportException {
		Apdu apdu = new Apdu();
        apdu.command[Apdu.CLA] = CLA_PROJECTAPPLET;
        apdu.command[Apdu.INS] = INS_RES_CARD;
        cad.exchangeApdu(apdu);
        
        if (apdu.getStatus() != 0x9000) {
            System.out.println("Erreur : status word different de 0x9000");
        } else {
            System.out.println("OK");
        }
	}
	
}
