package com.halty.diamondOreExchanger;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public class Database {
    private final DiamondOreExchanger plugin;
    private final Logger logger;
    private Firestore db;

    public Database(DiamondOreExchanger plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            InputStream serviceAccount = plugin.getResource("diamondexchangepluginbase-firebase-adminsdk-hyrnz-cdfd9ebe38.json");
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
            db = FirestoreClient.getFirestore();
        } catch (IOException e) {
            logger.severe("Не удалось инициализировать Firestore: " + e.getMessage());
        }
    }

    public double getBalance(UUID playerUUID) {
        try {
            DocumentSnapshot document = db.collection("player_balance").document(playerUUID.toString()).get().get();
            if (document.exists()) {
                return document.getDouble("balance");
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.warning("Ошибка при получении баланса игрока: " + e.getMessage());
        }
        return 0.0;
    }

    public void setBalance(UUID playerUUID, double balance) {
        Map<String, Object> data = new HashMap<>();
        data.put("balance", balance);
        db.collection("player_balance").document(playerUUID.toString()).set(data)
                .addListener(() -> {}, Runnable::run);
    }

    public HashMap<UUID, Double> getTopTenBalances() {
        HashMap<UUID, Double> balances = new HashMap<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection("player_balance")
                    .orderBy("balance", Query.Direction.DESCENDING)
                    .limit(10)
                    .get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                UUID playerUUID = UUID.fromString(document.getId());
                double balance = document.getDouble("balance");
                balances.put(playerUUID, balance);
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.warning("Ошибка при получении топ-10 балансов: " + e.getMessage());
        }
        return balances;
    }

    public boolean updatePlayerBalance(UUID playerUUID, double amountToAdd) {
        try {
            DocumentReference docRef = db.collection("player_balance").document(playerUUID.toString());
            db.runTransaction((Transaction transaction) -> {
                DocumentSnapshot snapshot = transaction.get(docRef).get();
                double newBalance = (snapshot.exists() ? snapshot.getDouble("balance") : 0.0) + amountToAdd;
                transaction.set(docRef, Collections.singletonMap("balance", newBalance));
                return null;
            }).get();
            return true;
        } catch (InterruptedException | ExecutionException e) {
            logger.warning("Ошибка при обновлении баланса игрока: " + e.getMessage());
            return false;
        }
    }

    public String getTelegramCode(UUID playerUUID) {
        try {
            DocumentSnapshot document = db.collection("player_balance").document(playerUUID.toString()).get().get();
            if (document.exists() && document.contains("telegram_code")) {
                return document.getString("telegram_code");
            } else {
                String code = UUID.randomUUID().toString();
                Map<String, Object> data = new HashMap<>();
                data.put("telegram_code", code);
                db.collection("player_balance").document(playerUUID.toString()).set(data, SetOptions.merge()).get();
                return code;
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.warning("Ошибка при получении/создании Telegram кода: " + e.getMessage());
            return null;
        }
    }
}