/*Saya Muhammad Muhammad Fadlul Hafiizh [2209889] mengerjakan soal latprak_7 dalam mata kuliah DPBO.
untuk keberkahanNya maka saya tidak melakukan kecurangan seperti yang telah dispesifikasikan, Aamiin */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;


public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    private Image backgroundImage, birdImage, lowerPipeImage, upperPipeImage; //inisialisasi semua asset image yang digunakan pada game
    private int frameWidth = 360, frameHeight = 640; //buat size frame antarmuka berukan 360x640
    Player player; //inisialisasi player
    //nilai titik horizontal start player selalu ada di seperdelapan dari lebar layar sehingga berada di sebelah kiri
    private int playerStartPosX = frameWidth/8;
    //nilai titik vertical start player berada ditengah tengah layar
    private int playerStartPosY = frameWidth/2;

    //nilai posisi horizontal pipe dari posisi lebar frame (pojok kanan)
    private int pipeStartPosX = frameWidth;

    //nilai posisi pipe pertama kali (diatas)
    private int pipeStartPosY = 0;

    //nilai ukuran player
    private int playerWidth = 34;
    private int playerHeight = 24;

    //nilai ukuran pipe
    private int pipeWidth = 64;
    private int pipeHeight = 512;
    ArrayList<Pipe> pipes; //untuk menampung semua pipa yang akan dibuat sepanjang game berjalan

    //timer untuk loop frame permainan dan generate pipa
    private Timer gameLoop;
    private Timer pipesCooldown;
    private int gravity = 1;//nilai gravitasi agar posisi vertical player selalu ditarik kebawah nantinya
    private boolean is_gameOver = false; //flag game berakhir atau tidak

    private JLabel scoreLabel; //label skoring untuk permainan atau message game over
    private float score; //untuk menampung nilai perolehan score

    public FlappyBird() {
        setPreferredSize(new Dimension(frameWidth, frameHeight)); //set frame sesuai ukuran
        setFocusable(true);
        addKeyListener(this); //listener setiap input keyboard
        setBackground(Color.blue);
        score = 0; //set score awal 0
        //load images
        backgroundImage = new ImageIcon(getClass().getResource("assets/background.png")).getImage();
        birdImage = new ImageIcon(getClass().getResource("assets/bird.png")).getImage();
        lowerPipeImage = new ImageIcon(getClass().getResource("assets/lowerPipe.png")).getImage();
        upperPipeImage = new ImageIcon(getClass().getResource("assets/upperPipe.png")).getImage();

        //tambahkan label skor pada permainan
        scoreLabel = new JLabel("Skor : " + String.valueOf((int)score));
        scoreLabel.setFont(scoreLabel.getFont().deriveFont(Font.BOLD, 25f));
        add(scoreLabel);

        //buat instance player sesuai posisi start dan ukurannya
        player = new Player(playerStartPosX, playerStartPosY, playerWidth, playerHeight, birdImage);
        pipes = new ArrayList<Pipe>(); //buat list untuk menampung pipa yang di generate sepanjang permainan
        pipesCooldown = new Timer(3000, new ActionListener() { //generate objek pipa setiap 3 detik
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipe();
            }
        });
        pipesCooldown.start();
        gameLoop = new Timer(1000/80, this); //perbarui frame render 80x perdetik
        gameLoop.start();
    }
    //memposisikan pipa (generate objek pipa)
    public void placePipe(){
        int randomPosY = (int) (pipeStartPosY - pipeHeight/4 - Math.random() * (pipeHeight/2)); //generate posisi vertical dari pipa
        int openingSpace = frameHeight/4; //sisakan seperempat dari height layar sebagai open space sebagai jalur lewat

        //masukan pada list tiap pipa atas dan bawah
        Pipe upperPipe = new Pipe(pipeStartPosX, randomPosY, pipeWidth, pipeHeight, upperPipeImage);
        pipes.add(upperPipe);

        Pipe lowerPipe = new Pipe(pipeStartPosX, (randomPosY + openingSpace + pipeHeight), pipeWidth, pipeHeight, lowerPipeImage);
        pipes.add(lowerPipe);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
//draw setiap asset dengan perubahan kooordinat posisi X dan Y nya
    public void draw(Graphics g){
        g.drawImage(backgroundImage, 0, 0, frameWidth, frameHeight, null);
        g.drawImage(player.getImage(),player.getPosX(), player.getPosY(), player.getWidth(), player.getHeight(), null);
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.getImage(), pipe.getPosX(), pipe.getPosY(), pipe.getWidth(), pipe.getHeight(), null);
        }
    }
//handle pergerakan burung dan pipa
    public void move(){
        if(!is_gameOver){//jika game belum berakhir

            //pengaruhi posisi Y burung agar tidak dapat melebihi frame dan berikan nilai gravitasi agar posisi Y bertambah (bergerak kebawah)
            player.setVelocityY(player.getVelocityY() + gravity);
            player.setPosY(player.getPosY() + player.getVelocityY());
            player.setPosY(Math.max(player.getPosY(),0));

            for (int i = 0; i < pipes.size(); i++) {
                Pipe pipe = pipes.get(i); //set koordinat baru semua pipa yang ada pada list agar bergerak ke kiri atau mengurangi koordinat x
                pipe.setPosX(pipe.getPosX() + pipe.getVelocityX());
                if (player.getPosX() > (pipe.getPosX() + pipe.getWidth()) && !pipe.isPassed()){ //cek apakah sebuah pipa sudah dilewati burung & pastikan pipa tersebut belum dilewati dengan cek isPassed
                    score += 0.5; //0.5 untuk masing masing pipa atas dan bawah
                    pipe.setPassed(true);
                    scoreLabel.setText("Skor : " + String.valueOf((int)score));
                }

                //ambil nilai koordinat pipa dan burung sebenarnya dengan mempertimbangkan height dan width nya
                int clearPlayerPosY = player.getPosY() + player.getHeight();
                int clearPlayerPosX = player.getPosX() + player.getWidth();
                int clearPipePosY = pipe.getPosY() + pipe.getHeight();
                int clearPipePosX = pipe.getPosX() + pipe.getWidth();

                //cek apakah posisi burung dan pipa bertabrakan?
                if (clearPlayerPosY > pipe.getPosY() && clearPlayerPosX > pipe.getPosX() && player.getPosX() < clearPipePosX && player.getPosY() < clearPipePosY){
                    is_gameOver = true;
                    scoreLabel.setFont(scoreLabel.getFont().deriveFont(Font.BOLD, 15f));
                    scoreLabel.setText("Game Over, Tekan R untuk restart");
                }
            }

            //cek apakah burung jatuh kebawah atau posisi y nya lewat dari frame heightnya
            if (player.getPosY() > frameHeight){
                is_gameOver = true;
                scoreLabel.setFont(scoreLabel.getFont().deriveFont(Font.BOLD, 15f));
                scoreLabel.setText("Game Over, Tekan R untuk restart");
            }
        }else{
            //bisa untuk menampilkan menu disini
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE){
            player.setVelocityY(-10);
        }
        if (is_gameOver){//pastikan bila game sudah berakhir barulah set event listener untuk tombol R
            if (e.getKeyCode() == KeyEvent.VK_R){ //reset game seperti baru dimulai kembali
                pipes.clear(); //kosongkan semua pipa yang pernah di generate
                player.setPosY(playerStartPosY); //set ulang posisi X dan Y burung sesuai start Position nya
                player.setPosX(playerStartPosX);
                is_gameOver = false; //tandai ulang game belum berakhir
                score = 0; //setel ulang score
                scoreLabel.setFont(scoreLabel.getFont().deriveFont(Font.BOLD, 25f)); //reset label ke ukuran dan nilai score awal
                scoreLabel.setText("Skor : " + String.valueOf((int)score));

            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
