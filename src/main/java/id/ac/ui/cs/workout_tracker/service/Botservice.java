package id.ac.ui.cs.workout_tracker.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.event.source.UserSource;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.profile.UserProfileResponse;

import id.ac.ui.cs.workout_tracker.model.User;
import id.ac.ui.cs.workout_tracker.model.Workout;
import id.ac.ui.cs.workout_tracker.repository.UserRepository;

import java.util.concurrent.ExecutionException;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class Botservice {
    public Source source;

    @Autowired
    private LineMessagingClient lineMessagingClient;

    @Autowired
    private DatabaseService dbService;



    @Autowired
    private UserRepository userRepository;

    /** Get User From Line.
     */
    public UserProfileResponse getProfile(String userId) {
        try {
            return lineMessagingClient.getProfile(userId).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private void reply(ReplyMessage replyMessage) {
        try {
            lineMessagingClient.replyMessage(replyMessage).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void reply(String replyToken, Message message) {
        ReplyMessage replyMessage = new ReplyMessage(replyToken, message);
        reply(replyMessage);
    }

    /** Method To ReplyText.
     */
    public void replyText(String replyToken, String messageText) {
        TextMessage textMessage = new TextMessage(messageText);
        reply(replyToken, textMessage);
    }




    /** Method To Handle Message Event.
     */
    public void handleMessageEvent(MessageEvent event) {
        String replyToken      = event.getReplyToken();
        MessageContent content = event.getMessage();
        Source source          = event.getSource();

        if (content instanceof TextMessageContent) {
            handleTextMessage(replyToken, (TextMessageContent) content, source);
        } else {
            welcomeMessage(replyToken);
        }
    }

    /** Method To Handle Text Message.
     */
    public void handleTextMessage(String replyToken, TextMessageContent content, Source source) {
        if (source instanceof UserSource) {
            handleOneOnOneChats(replyToken, content.getText());
        }
    }

     /** Method To Handle One On One Chat.
     */
    public void handleOneOnOneChats(String replyToken, String textMessage) {
        String msgText = textMessage.toLowerCase();
        if (msgText.contains("/help")) {
            helpMessage(replyToken);
        } else if (msgText.contains("/reg")) {
            register(replyToken, msgText);
        } else if (msgText.contains("/jadwal")) {
            showJadwal(replyToken);
        } else if (msgText.contains("/create") && msgText.trim().length() < 8) {


            createMessage(replyToken);
        } else if (msgText.contains("/create") && msgText.trim().length() > 8) {
            log.info("message for /create: {}", msgText);
            createJadwal(replyToken, msgText);
        } else if  (msgText.contains("/delete") && msgText.trim().length() < 7) {
            deleteMessage(replyToken);
        } else if  (msgText.contains("/delete") && msgText.trim().length() > 7) {
            deleteWorkout(replyToken, textMessage);
        } else if (msgText.contains("/clear")) {
            clearWorkout(replyToken);
        } else {
            String welcome = "Selamat datang di Fitbot! ";
            String help = "Untuk melihat bantuan ketik '/help'.";
            replyText(replyToken, welcome + help);
        }
    }

    /**
        Method for registration 
    */
    public void register(String replyToken, String msgText) {

        String[] words = msgText.trim().split("/");
        log.info("register method, words: {}",words);
        checkGender(replyToken, words); 
    }

    public void checkGender(String replyToken, String[] words) {

        try {
            String gender = words[2];
            if (gender.equals("L") || gender.equals("P")) {
                validGender(replyToken, words);
            } else {
                invalidGender(replyToken);
            }
        } catch (Exception e) {
            String msg = "Gender yang kamu masukkan tidak valid! masukkan hanya L atau P!";
            replyText(replyToken, msg);
        }
    }

    public void invalidGender(String replyToken) {
        String msg = "Gender yang kamu masukkan tidak valid! Silahkan masukkan antara L atau P!";
        replyText(replyToken, msg);
    }

    public void validGender(String replyToken, String[] words) {
        log.info("validGender, words: {}",words);
        String beratBadan = words[3];
        try {
            if (Integer.parseInt(beratBadan) > 0) {
                validBeratBadan(replyToken, words);
            } else {
                invalidBeratBadan(replyToken);
            }
        } catch (Exception e) {
            String msg = "Berat badan yang kamu masukkan tidak valid! Harap masukkan angka!";
            replyText(replyToken, msg);
        }
    }

    public void invalidBeratBadan(String replyToken) {
        String msg = "Berat badan yang kamu masukkan tidak valid! Harap masukkan sebuah angka yang lebih besar dari 0!";
        replyText(replyToken, msg);
    }

    public void validBeratBadan(String replyToken, String[] words) {
        String tinggiBadan = words[4];
        try {
            if (Integer.parseInt(tinggiBadan) > 0) {
                checkSender(replyToken);
            } else {
                invalidTinggiBadan(replyToken);
            }
        } catch (Exception e) {
            String msg = "Tinggi badan yang kamu masukkan tidak valid! Harap masukkan angka!";
            replyText(replyToken, msg);
        }
    }

    public void invalidTinggiBadan(String replyToken) {
        String msg = "Tinggi badan yang kamu masukkan tidak valid! Harap masukkan sebuah angka yang lebih besar dari 0!";
        replyText(replyToken, msg);
    }
    
    public void checkSender(String replyToken) {
        String userId = source.getSenderId();
        UserProfileResponse sender = getProfile(userId);
        
        if (sender == null) {
            String msg = "Halo! Kamu belum menambahkan Fitbot sebagai teman!";
            replyText(replyToken, msg);
        } else {
            checkDisplayNameAndLineiD(replyToken, sender);
        }
    }

    public void checkDisplayNameAndLineiD(String replyToken, UserProfileResponse sender) {
        if (!sender.getDisplayName().isEmpty()) {
            checkRegistration(replyToken, sender);
        } else {
            String msg = "Halo! Kamu belum menambahkan Fitbot sebagai teman!";
            replyText(replyToken, msg);
        }
    }

    public void checkRegistration(String replyToken, UserProfileResponse sender) {
        if (dbService.regLineID(sender.getUserId(), sender.getDisplayName()) != 0) {
            String msg = "Halo! Kamu sudah terdaftar sebagai Fitbot!";
            replyText(replyToken, msg);
        } else {
            String msg = "Halo! Sayang sekali kamu gagal bergabung dengan Fitbot. Silahkan coba lagi!";
            replyText(replyToken, msg);
        }
    }

    /** Method To Create Greeting Message.
     */
    public void welcomeMessage(String replyToken) {
        String welcome = "Selamat datang di Fitbot!\n\n" +
                "Sebelum dapat menggunakan Fitbot silahkan melakukan registrasi terlebih dahulu " +
                "dengan ketik ‘/reg/gender(L/P)/berat badan(kg)/tinggi badan(cm)’\n" +
                "Contoh: /reg/L/52/158";
        replyText(replyToken, welcome);
    }

    public void menuMessage(String replyToken) {
        String menu = "Berikut fitur yang dapat Anda gunakan pada Fitbot dan cara memanggilnya:\n" +
        "1. Menjadwalkan workout: ‘/create’\n" +
        "2. Melihat history: ‘/history’\n" +
        "3. Melihat jadwal workout: ‘/jadwal’\n" + 
        "4. Menghapus workout pada hari tertentu: ‘/delete’\n" +
        "5. Menghapus seluruh workout: ‘/clear’\n" +
        "6. Mengupdate berat dan tinggi badan: ‘/update’\n" +
        "7. Help: ‘/help’\n\n" +
        "Perlu diperhatikan Anda tidak perlu mengetik tanda petik untuk menggunakan fitur yang Anda inginkan";

        replyText(replyToken, menu);
    }

    public void createMessage(String replyToken) {
        String jadwal = "Berikut workout yang dapat Anda lakukan:\n" +
        "1. PushUp\n" +
        "2. PullUp\n" +
        "3. SitUp\n" +
        "4. Jump\n" +
        "5. Plank\n" +
        "6. Running\n" +
        "7. Jogging\n" + 
        "8. Cycling\n\n" +
        "Berikut format yang perlu Anda ikuti untuk menyusun jadwal workout: \n" +
        "/create {Hari},{Nama Exercise}/{Jumlah Set}/{Jumlah Rep}\n\n" +
        "NB: perlu diperhatikan jika ingin menambahkan lebih dari satu workout dalam satu hari pisahkan dengan koma (,)\n" +
        "Contoh: /create Senin,PushUp/3/5,SitUp/2/4";

        replyText(replyToken, jadwal);
    }

    public void showJadwal(String replyToken) {
        String userid = source.getSenderId();
        // UserProfileResponse sender = getProfile(userid);

        String show = "Berikut jadwal workout Anda untuk setiap minggunya";
        String senin = "1. Senin: \n";
        String selasa = "2. Selasa: \n";
        String rabu = "3. Rabu: \n";
        String kamis = "4. Kamis: \n";
        String jumat = "5. Jumat: \n";
        String sabtu = "6. Sabtu: \n";
        String minggu = "7. Minggu: \n";

        for (User user: userRepository.findAll()) {
            if (user.getUserId().equals(userid)) {
                for (Workout wo: user.getListWorkout()) {
                    if (wo.getDay().equals("Senin")) {
                        senin += jadwalPerHari(wo);
                    } else if (wo.getDay().equals("Selasa")) {
                        selasa += jadwalPerHari(wo);
                    } else if (wo.getDay().equals("Rabu")) {
                        rabu += jadwalPerHari(wo);
                    } else if (wo.getDay().equals("Kamis")) {
                        kamis += jadwalPerHari(wo);
                    } else if (wo.getDay().equals("Jumat")) {
                        jumat += jadwalPerHari(wo);
                    } else if (wo.getDay().equals("Sabtu")) {
                        sabtu += jadwalPerHari(wo);
                    } else if (wo.getDay().equals("Minggu")) {
                        minggu += jadwalPerHari(wo);
                    }
                }
            }
        }

        String allDays = senin + selasa + rabu + kamis + jumat + sabtu + minggu;

        replyText(replyToken, show + allDays);
    }

    public String jadwalPerHari(Workout wo) {
        String workout = "  -Workout: " + wo.getName() + "\n" +
        "   -Jumlah Set: " + wo.getSets() + "\n" +
        "   -Jumlah Rep: " + wo.getReps();

        return workout;
    }

    public void createJadwal(String replyToken, String msgText) {
        log.info("method createJadwal, msg: {}", msgText);
        String exceptMsg = "Harap perhatikan format yang perlu dikirimkan";
        String userid = source.getSenderId();
        User pengguna = userRepository.findByUserId(userid);
        Workout newWorkout;

        try {
            String newText = msgText.split(" ")[1];
            String day = newText.split(",")[0];
            String[] workoutDetail = newText.split(",")[1].split("/");
            
            if(checkday(day)){
                if(checkworkout(workoutDetail[0])){
                    newWorkout = new Workout(day, workoutDetail[0], Integer.parseInt(workoutDetail[1]), Integer.parseInt(workoutDetail[2]));
                    pengguna.getListWorkout().add(newWorkout);
                }else{
                    replyText(replyToken, exceptMsg);
                }
            }else{
                replyText(replyToken, exceptMsg);
            }

        }catch (Exception e){
            replyText(replyToken, exceptMsg);
        }
    }

    public boolean checkday(String day) {
        if (day.toLowerCase().equals("senin")) {
            return true;
        } else if (day.toLowerCase().equals("selasa")) {
            return true;
        } else if (day.toLowerCase().equals("rabu")) {
            return true;
        } else if (day.toLowerCase().equals("kamis")) {
            return true;
        } else if (day.toLowerCase().equals("jumat")) {
            return true;
        } else if (day.toLowerCase().equals("sabtu")) {
            return true;
        } else if (day.toLowerCase().equals("minggu")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkworkout(String selectedWorkout){
        if (selectedWorkout.toLowerCase().equals("PushUp")){
            return true;
        }else if (selectedWorkout.toLowerCase().equals("PullUp")){
            return true;
        }else if (selectedWorkout.toLowerCase().equals("SitUp")){
            return true;
        }else if (selectedWorkout.toLowerCase().equals("jump")){
            return true;
        }else if (selectedWorkout.toLowerCase().equals("plank")){
            return true;
        }else if (selectedWorkout.toLowerCase().equals("running")){
            return true;
        }else if (selectedWorkout.toLowerCase().equals("jogging")){
            return true;
        }else if (selectedWorkout.toLowerCase().equals("cycling")){
            return true;
        }else{
            return false;
        }
    }

    public void helpMessage(String replyToken) {
        String help = "Halo! Admin akan memberikan beberapa panduan dalam menggunakan Fitbot:\n" +
        "1. Fitbot menyediakan beberapa fitur, yaitu menjadwalkan workout perhari dalam 1 minggu, melihat history, melihat jadwal workout, meghapus jadwal workout, dan mengupdate berat maupun tinggi badan\n" +
        "2. Untuk melihat list command yang dapat dilakukan pada fitbot silahkan ketik ‘/menu’\n" +
        "3. Jika kamu ingin mengubah jadwal, silahkan delete jadwal workout yang kamu inginkan terlebih dahulu dengan mengetik ‘/delete’. Setelah itu kamu bisa membuat jadwal yang baru dengan mengetik ‘/create’";

        replyText(replyToken, help);
    }

    private void deleteMessage(String replyToken) {
        String delete =  "Untuk menghapus seluruh workout pada hari tertentu ikuti format berikut:\n" +
        "“/delete {Hari}\n" +
        "Contoh: /delete Senin";

        replyText(replyToken, delete);
    }

    private void clearWorkout(String replyToken) {
        String userid = source.getSenderId();
        List<Workout> newListWorkout = new ArrayList<>();
        for (User user: userRepository.findAll()) {
            if (user.getUserId().equals(userid)) {
                user.setListWorkout(newListWorkout);
                replyText(replyToken, "Berhasil menghapus seluruh workout");
            }
        }
    }

    
    private void deleteWorkout(String replyToken, String text) {
        String day = text.split(" ")[1];
        String userid = source.getSenderId();

        if (checkday(day)) {
            for (User user: userRepository.findAll()) {
                if (user.getUserId().equals(userid)) {
                    List<Workout> newListWorkout = new ArrayList<>();
                    for (Workout wo: user.getListWorkout()) {
                        if (!wo.getDay().equals(day))
                            newListWorkout.add(wo);
                    }
                    user.setListWorkout(newListWorkout);
                    replyText(replyToken, "Berhasil menghapus seluruh workout pada hari " + day);
                }
            }
        } else {
            replyText(replyToken, "Input Hari Salah");
        }

    }

}