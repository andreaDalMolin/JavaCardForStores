package be.masi_liege_g2_2223;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class MainApplication {
	
	private static final String host = "localhost";
	private static final int port = 9025;

	public static void main(String[] args) throws Exception {
	    Socket socket;
	    try {
	        socket = new Socket(host, port);
	        socket.setTcpNoDelay(true);
	    } catch (IOException e) {
	        System.out.println("Impossible de se connecter � la carte");
	        return;
	    }

	    JCManager jcManager = new JCManager(socket);
	    jcManager.connect();

	    boolean fin = false;
	    Scanner scanner = new Scanner(System.in);

	    while (!fin) {   	
	        System.out.println();
	        System.out.println("Application client JavaCard");
	        System.out.println("---------------------------");
	        System.out.println();
	        
	        int id = jcManager.getId();
	        if (id == 0) {
				System.out.println("---------------------------");
				System.out.println("No ID found. Initialising id...");
				System.out.println("---------------------------");

				//TODO get jwt from API
				String response = APIClientUtil.login("https://192.168.0.246:443/auth/signin",
						"user_card", "password_card");

				try {
					ObjectMapper objectMapper = new ObjectMapper();
					JsonNode jsonNode = objectMapper.readTree(response);

					String token = jsonNode.get("token").asText();
					System.out.println("Token: " + token);
				} catch (JsonMappingException e) {
					System.out.println("");;
				}

				//TODO get id from username


				jcManager.setId(1);
	        } else {
	        	// Synchro points
	        }

	        System.out.println("1- Interroger le compteur");
	        System.out.println("2- Incrementer le compteur");
	        System.out.println("3- Decrementer le compteur");
	        System.out.println("4- Reinitialiser le compteur");
	        System.out.println("5- Reinitialiser la carte");
	        System.out.println("6- Quitter");
	        System.out.println("Votre choix :");

	        int choix = scanner.nextInt();
	        scanner.nextLine();

	        switch (choix) {
	            case 1:
	                int points = jcManager.getPoints();
	                if (points != -1) {
	                    System.out.println("Valeur du compteur : " + points);
	                }
	                break;

	            case 2:
	                int pointsToAdd;
	                do {
	                    System.out.print("Entrez un nombre entre 1 et 50 : ");
	                    pointsToAdd = scanner.nextInt();

	                    if (pointsToAdd < 1 || pointsToAdd > 50) {
	                        System.out.println("Veuillez entrer une valeur entre 1 et 50.");
	                    }
	                } while (pointsToAdd < 1 || pointsToAdd > 50);

	                try {
	                    int currentPoints = jcManager.getPoints();
	                    if (currentPoints != -1) {
	                        int totalPoints = currentPoints + pointsToAdd;
	                        jcManager.setPoints(totalPoints);
	                    }
	                } catch (Exception e) {
	                    System.out.println("Erreur lors de la somme : " + e.getMessage());
	                }
	                break;

	            case 3:
	                int pointsToDeduct;
	                do {
	                    System.out.print("Entrez un nombre entre 1 et 50 : ");
	                    pointsToDeduct = scanner.nextInt();

	                    if (pointsToDeduct < 1 || pointsToDeduct > 50) {
	                        System.out.println("Veuillez entrer une valeur entre 1 et 50.");
	                    }
	                } while (pointsToDeduct < 1 || pointsToDeduct > 50);

	                try {
	                    int currentPoints = jcManager.getPoints();
	                    if (currentPoints != -1) {
	                        int totalPoints = currentPoints - pointsToDeduct;
	                        jcManager.setPoints(totalPoints);
	                    }
	                } catch (Exception e) {
	                    System.out.println("Erreur lors de la d�duction : " + e.getMessage());
	                }
	                break;

	            case 4:
	            	jcManager.resetPoints();
	                break;

	            case 5:
	                jcManager.resetCard();
	                break;
	            case 6:
	                fin = true;
	                break;
	        }
	    }

	    scanner.close();
	    jcManager.disconnect();
	    socket.close();
	}

}
