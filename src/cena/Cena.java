package cena;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import java.util.ArrayList;
import textura.Textura;
import com.jogamp.opengl.util.awt.TextRenderer; //ADICIONADO
import java.awt.Color; //ADICIONADO
import java.awt.Font; //ADICIONADO
/**
 *
 * @author Kakugawa
 */
public class Cena implements GLEventListener{    
    // Variaveis do Modelo
    private float xMin, xMax, yMin, yMax, zMin, zMax;
    GLU glu;
    
    // Variaveis de Textura
    private float limite;
    private Textura textura = null;
    private int totalTextura = 4;
    private static final String TEXTURA_ASTEROIDE = "imagens/TexturaAsteroide.jpg";
    private static final String TEXTURA_METAL_OVNI = "imagens/TexturaOvniMetal.jpg";
    private static final String TEXTURA_VIDRO_OVNI = "imagens/TexturaVidro.png";
    private static final String TEXTURA_ESPACO = "imagens/TexturaEspaco.png";
    
    // Variaveis do calculo de tempo
    private long lastFrame;
    private double deltaT, timerAsteroide;
    
    // Objéto do Ovni
    private OvniGato ovni;
    
    // Lista de Asteroides
    ArrayList<Asteroide> listaAsteroide;
    
    // Objeto de Texto
    private TextRenderer textRenderer;
    
    //Variaveis de Pontuação
    private int pontuacao;
    
    //Variaveis de Menu
    private boolean pausadoMenu, sairMenu, morteMenu;
    private boolean opVoltarPause, opMenuPause, opSairPause, opSimSair, opNaoSair, opTentarMorte, opMenuMorte, opSairMorte;
    
    // Variaveis de Tela
    private boolean telaJogo, telaMenu, telaControles, telaCreditos, opJogarMenu, opControlesMenu, opCreditosMenu, opSairMenu;
    
    @Override
    public void init(GLAutoDrawable drawable) {
        //dados iniciais da cena
        glu = new GLU();
        GL2 gl = drawable.getGL().getGL2(); //ADICIONADO
        
        //Estabelece as coordenadas do SRU (Sistema de Referencia do Universo)
        xMin = yMin = zMin = -1;
        xMax = yMax = zMax = 1;
        
        // Variaveis de Textura
        limite = 256;
        textura = new Textura(totalTextura);
        
        // Habilitar Profundidade
        gl.glEnable(GL2.GL_DEPTH_TEST);
        
        //Criação do Objeto de texto
        textRenderer = new TextRenderer(new Font("Arial", Font.PLAIN, 28));
        
        //Inicia as variáveis de jogo
        lastFrame = 0;
        deltaT = 0;
        trocarTela(1);
    }

    @Override
    public void display(GLAutoDrawable drawable) {  
        //obtem o contexto Opengl
        GL2 gl = drawable.getGL().getGL2();
        GLUT glut = new GLUT();
        //ADICIONADO (Habilita o buffer de profundidade)
        //gl.glEnable(GL2.GL_DEPTH_TEST);
        //define a cor da janela (R, G, G, alpha)
        gl.glClearColor(0, 0, 0, 1);        
        //limpa a janela com a cor especificada
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);       
        gl.glLoadIdentity(); //lê a matriz identidade
        
        // Ativa a Iluminação
        iluminacao(gl);
        
        // Desenha o Plano de fundo
        desenhaFundo(gl);
        
        // Calculo do delta
        calcDeltaT();
        
        // Determina qual tela será carregada
        if((telaJogo)&&(ovni != null)){
            timerSpawn();
            calcFisica();
            desenhaJogo(gl, glut);
        }else if(telaMenu){
            desenhaMenu(gl);
        }else if(telaControles){
            desenhaControles(gl);
        }else if(telaCreditos){
            desenhaCreditos(gl);
        }
        
        gl.glFlush(); 
             
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {    
        //obtem o contexto grafico Opengl
        GL2 gl = drawable.getGL().getGL2();  
        
        //evita a divisão por zero
        if(height == 0) height = 1;
        //calcula a proporção da janela (aspect ratio) da nova janela
        float aspect = (float) width / height;
        
        //seta o viewport para abranger a janela inteira
        gl.glViewport(0, 0, width, height);
                
        //ativa a matriz de projeção
        gl.glMatrixMode(GL2.GL_PROJECTION);      
        gl.glLoadIdentity(); //lê a matriz identidade
        
        //Projeção ortogonal
        //true:   aspect >= 1 configura a altura de -1 para 1 : com largura maior
        //false:  aspect < 1 configura a largura de -1 para 1 : com altura maior
        if(width >= height)            
            gl.glOrtho(xMin * aspect, xMax * aspect, yMin, yMax, zMin, zMax);
        else        
            gl.glOrtho(xMin, xMax, yMin / aspect, yMax / aspect, zMin, zMax);
                
        //ativa a matriz de modelagem
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity(); //lê a matriz identidade
        //System.out.println("Reshape: " + width + ", " + height);
    }
       
    @Override
    public void dispose(GLAutoDrawable drawable) {}
    
    
    // Metodo que Calcula a fisica dentro do jogo
    public void calcFisica(){
        if((!pausadoMenu)&&(!morteMenu)){
            ovni.calcularGravidade(deltaT);
            ovni.aplicaAnimacao(deltaT);
            ovni.moverOvni(deltaT);
            for(int i = 0; i < listaAsteroide.size(); i++){
                listaAsteroide.get(i).moverAsteroide(deltaT);
                if(listaAsteroide.get(i).getPosicaoX() <= -2.5){
                    listaAsteroide.remove(i);
                }
                if (ovni.verificarColisao(listaAsteroide.get(i).getPosicaoX(), listaAsteroide.get(i).getPosicaoY(), listaAsteroide.get(i).getTamanho())){
                    chamarMorteMenu();
                }
                if (ovni.verificarPonto(listaAsteroide.get(i).getPosicaoX(), listaAsteroide.get(i).getValePonto())){
                    fezPonto();
                    listaAsteroide.get(i).fezPonto();
                }
            }
            if (ovni.colisaoBorda()){
                chamarMorteMenu();
            }
        }
        
    }
    
    // Metodo que calcula a variação de tempo entre um frame e outro
    public void calcDeltaT(){
        if(lastFrame == 0){
            lastFrame = System.currentTimeMillis();
        }else{
            deltaT = System.currentTimeMillis() - lastFrame;
            deltaT /= 1000;
            lastFrame = System.currentTimeMillis();
        }
    }
    
    // Timer de invocação de asteroides
    public void timerSpawn(){
        if((!pausadoMenu)&&(!morteMenu)){
            timerAsteroide += deltaT;
            if (timerAsteroide >= 2.25){
                spawnAsteroide();
                timerAsteroide = 0.0;
            }
        }
    }
    
    // Metodo para criação de novos asteroides
    public void spawnAsteroide(){
        listaAsteroide.add(new Asteroide());
    }
    
    // Metodo para marcação de pontos
    public void fezPonto(){
        pontuacao++;
    }
    
    // Metodo para desenhar o fundo do jogo
    public void desenhaFundo(GL2 gl){
        gl.glPushMatrix();
            gl.glMatrixMode(GL2.GL_TEXTURE);
                gl.glLoadIdentity();                      
                gl.glScalef(limite/textura.getWidth(), limite/textura.getHeight(), limite);           
            gl.glMatrixMode(GL2.GL_MODELVIEW);
            textura.setAutomatica(false);
            textura.setFiltro(GL2.GL_NEAREST);
            textura.setModo(GL2.GL_DECAL);
            textura.setWrap(GL2.GL_REPEAT);
            textura.gerarTextura(gl, TEXTURA_ESPACO, 0);
            gl.glColor3d(1, 1, 1);
            gl.glBegin(GL2.GL_POLYGON);
                gl.glTexCoord2d(0, 0.5);
                gl.glVertex3d(-2, -2, -0.3);
                gl.glTexCoord2d(0.5, 0.5);
                gl.glVertex3d(-2, 2, -0.3);
                gl.glTexCoord2d(0.5, 0);
                gl.glVertex3d(2, 2, -0.3);
                gl.glTexCoord2d(0, 0);
                gl.glVertex3d(2, -2, -0.3);
            gl.glEnd();
            textura.desabilitarTextura(gl, 0);
        gl.glPopMatrix();
    }
    
    // Metodo que desenha a tela de menu
    public void desenhaMenu(GL2 gl){
        gl.glColor3d(1, 1, 1);
        gl.glBegin(GL2.GL_POLYGON);
            gl.glVertex3d(-0.355, -0.155, 0.3);
            gl.glVertex3d(-0.355, 0.005, 0.3);
            gl.glVertex3d(0.355, 0.005, 0.3);
            gl.glVertex3d(0.355, -0.155, 0.3);
        gl.glEnd();
        gl.glColor3d(0.098, 0.102, 0.341);
        gl.glBegin(GL2.GL_POLYGON);
            gl.glVertex3d(-0.35, -0.15, 0.31);
            gl.glVertex3d(-0.35, 0, 0.31);
            gl.glVertex3d(0.35, 0, 0.31);
            gl.glVertex3d(0.35, -0.15, 0.31);
        gl.glEnd();
        if(opJogarMenu){
            gl.glColor3d(0.353, 0.004, 0.008);
            gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex3d(-0.35, -0.15, 0.32);
                gl.glVertex3d(-0.35, 0, 0.32);
                gl.glVertex3d(0.35, 0, 0.32);
                gl.glVertex3d(0.35, -0.15, 0.32);
            gl.glEnd();
        }
        gl.glColor3d(1, 1, 1);
        gl.glBegin(GL2.GL_POLYGON);
            gl.glVertex3d(-0.355, -0.355, 0.3);
            gl.glVertex3d(-0.355, -0.195, 0.3);
            gl.glVertex3d(0.355, -0.195, 0.3);
            gl.glVertex3d(0.355, -0.355, 0.3);
        gl.glEnd();
        gl.glColor3d(0.098, 0.102, 0.341);
        gl.glBegin(GL2.GL_POLYGON);
            gl.glVertex3d(-0.35, -0.35, 0.31);
            gl.glVertex3d(-0.35, -0.2, 0.31);
            gl.glVertex3d(0.35, -0.2, 0.31);
            gl.glVertex3d(0.35, -0.35, 0.31);
        gl.glEnd();
        if(opControlesMenu){
            gl.glColor3d(0.353, 0.004, 0.008);
            gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex3d(-0.35, -0.35, 0.32);
                gl.glVertex3d(-0.35, -0.2, 0.32);
                gl.glVertex3d(0.35, -0.2, 0.32);
                gl.glVertex3d(0.35, -0.35, 0.32);
            gl.glEnd();
        }
        gl.glColor3d(1, 1, 1);
        gl.glBegin(GL2.GL_POLYGON);
            gl.glVertex3d(-0.355, -0.555, 0.3);
            gl.glVertex3d(-0.355, -0.395, 0.3);
            gl.glVertex3d(0.355, -0.395, 0.3);
            gl.glVertex3d(0.355, -0.555, 0.3);
        gl.glEnd();
        gl.glColor3d(0.098, 0.102, 0.341);
        gl.glBegin(GL2.GL_POLYGON);
            gl.glVertex3d(-0.35, -0.55, 0.31);
            gl.glVertex3d(-0.35, -0.4, 0.31);
            gl.glVertex3d(0.35, -0.4, 0.31);
            gl.glVertex3d(0.35, -0.55, 0.31);
        gl.glEnd();
        if(opCreditosMenu){
            gl.glColor3d(0.353, 0.004, 0.008);
            gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex3d(-0.35, -0.55, 0.32);
                gl.glVertex3d(-0.35, -0.4, 0.32);
                gl.glVertex3d(0.35, -0.4, 0.32);
                gl.glVertex3d(0.35, -0.55, 0.32);
            gl.glEnd();
        }
        gl.glColor3d(1, 1, 1);
        gl.glBegin(GL2.GL_POLYGON);
            gl.glVertex3d(-0.355, -0.755, 0.3);
            gl.glVertex3d(-0.355, -0.595, 0.3);
            gl.glVertex3d(0.355, -0.5950, 0.3);
            gl.glVertex3d(0.355, -0.755, 0.3);
        gl.glEnd();
        gl.glColor3d(0.098, 0.102, 0.341);
        gl.glBegin(GL2.GL_POLYGON);
            gl.glVertex3d(-0.35, -0.75, 0.31);
            gl.glVertex3d(-0.35, -0.6, 0.31);
            gl.glVertex3d(0.35, -0.6, 0.31);
            gl.glVertex3d(0.35, -0.75, 0.31);
        gl.glEnd();
        if(opSairMenu){
            gl.glColor3d(0.353, 0.004, 0.008);
            gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex3d(-0.35, -0.75, 0.32);
                gl.glVertex3d(-0.35, -0.6, 0.32);
                gl.glVertex3d(0.35, -0.6, 0.32);
                gl.glVertex3d(0.35, -0.75, 0.32);
            gl.glEnd();
        }
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.WHITE);
            textRenderer.draw("Flappy Ovni", 520, 600);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.WHITE);
            textRenderer.draw("Jogar", 520, 430);
        textRenderer.endRendering(); 
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.WHITE);
            textRenderer.draw("Controles", 520, 335);
        textRenderer.endRendering(); 
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.WHITE);
            textRenderer.draw("Creditos", 520, 240);
        textRenderer.endRendering(); 
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.WHITE);
            textRenderer.draw("Sair", 520, 145);
        textRenderer.endRendering();
        if(sairMenu){
            desenhaSairMenu(gl);
        }
        
    }
    
    // Metodo que desenha o jogo
    public void desenhaJogo(GL2 gl, GLUT glut){
        ovni.desenhaOvni(gl, glut, textura, TEXTURA_METAL_OVNI, TEXTURA_VIDRO_OVNI, limite);
        for(int i = 0; i < listaAsteroide.size(); i++){
            listaAsteroide.get(i).desenhaAsteroide(gl, glut, textura, TEXTURA_ASTEROIDE, limite);
        }
        desenhaPontuacao();
        if(pausadoMenu){
            desenhaPausadoMenu(gl);
        }
        if(morteMenu){
            desenhaMorteMenu(gl);
        }
        if(sairMenu){
            desenhaSairMenu(gl);
        }
    }
    
    // Metodo que desenha a tela de controles
    public void desenhaControles(GL2 gl){
        gl.glColor3d(1, 1, 1);
        gl.glPushMatrix();
            gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex3d(-0.405, -0.105, 0.3);
                gl.glVertex3d(-0.405, 0.405, 0.3);
                gl.glVertex3d(0.405, 0.4050, 0.3);
                gl.glVertex3d(0.405, -0.105, 0.3);
            gl.glEnd();
        gl.glPopMatrix();
        gl.glColor3d(0.098, 0.102, 0.341);
        gl.glPushMatrix();
            gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex3d(-0.4, -0.1, 0.31);
                gl.glVertex3d(-0.4, 0.4, 0.31);
                gl.glVertex3d(0.4, 0.4, 0.31);
                gl.glVertex3d(0.4, -0.1, 0.31);
            gl.glEnd();
        gl.glPopMatrix();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.WHITE);
            textRenderer.draw("Controles", 520, 600);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.WHITE);
            textRenderer.draw("Espaço: Pular", 520, 550);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.WHITE);
            textRenderer.draw("Esc: Pausar Jogo", 520, 500);
        textRenderer.endRendering();
    }
    
    // Metodo que desenha a tela de creditos
    public void desenhaCreditos(GL2 gl){
        gl.glColor3d(1, 1, 1);
        gl.glPushMatrix();
            gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex3d(-0.605, -0.205, 0.3);
                gl.glVertex3d(-0.605, 0.405, 0.3);
                gl.glVertex3d(0.605, 0.405, 0.3);
                gl.glVertex3d(0.605, -0.2050, 0.3);
            gl.glEnd();
        gl.glPopMatrix();
        gl.glColor3d(0.098, 0.102, 0.341);
        gl.glPushMatrix();
            gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex3d(-0.6, -0.2, 0.31);
                gl.glVertex3d(-0.6, 0.4, 0.31);
                gl.glVertex3d(0.6, 0.4, 0.31);
                gl.glVertex3d(0.6, -0.2, 0.31);
            gl.glEnd();
        gl.glPopMatrix();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.WHITE);
            textRenderer.draw("Creditos", 435, 630);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.WHITE);
            textRenderer.draw("Desenvolvedores:", 435, 530);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.WHITE);
            textRenderer.draw("Matheus Martins Garcia,", 435, 480);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.WHITE);
            textRenderer.draw("Miguel Rios da Silva", 435, 430);
        textRenderer.endRendering();
    }
    
    // Metodo que desenha a Pontuação na tela
    public void desenhaPontuacao(){
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.WHITE);
            textRenderer.draw(pontuacao+"", 620, 900);
        textRenderer.endRendering();
    }
    
    // Metodo que desenha o menu de pause
    public void desenhaPausadoMenu(GL2 gl){
        gl.glColor3d(1, 1, 1);
        gl.glPushMatrix();
            gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex3d(-0.405, -0.455, 0.3);
                gl.glVertex3d(-0.405, 0.505, 0.3);
                gl.glVertex3d(0.405, 0.505, 0.3);
                gl.glVertex3d(0.405, -0.455, 0.3);
            gl.glEnd();
        gl.glPopMatrix();
        gl.glColor3d(0.098, 0.102, 0.341);
        gl.glPushMatrix();
            gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex3d(-0.4, -0.45, 0.31);
                gl.glVertex3d(-0.4, 0.5, 0.31);
                gl.glVertex3d(0.4, 0.5, 0.31);
                gl.glVertex3d(0.4, -0.45, 0.31);
            gl.glEnd();
        gl.glPopMatrix();
        if(opVoltarPause){
            gl.glColor3d(0.353, 0.004, 0.008);
            gl.glPushMatrix();
                gl.glBegin(GL2.GL_POLYGON);
                    gl.glVertex3d(-0.4, -0.15, 0.32);
                    gl.glVertex3d(-0.4, 0, 0.32);
                    gl.glVertex3d(0.4, 0, 0.32);
                    gl.glVertex3d(0.4, -0.15, 0.32);
                gl.glEnd();
            gl.glPopMatrix();
        }
        if(opMenuPause){
            gl.glColor3d(0.353, 0.004, 0.008);
            gl.glPushMatrix();
                gl.glBegin(GL2.GL_POLYGON);
                    gl.glVertex3d(-0.4, -0.30, 0.32);
                    gl.glVertex3d(-0.4, -0.15, 0.32);
                    gl.glVertex3d(0.4, -0.15, 0.32);
                    gl.glVertex3d(0.4, -0.30, 0.32);
                gl.glEnd();
            gl.glPopMatrix();
        }
        if(opSairPause){
            gl.glColor3d(0.353, 0.004, 0.008);
            gl.glPushMatrix();
                gl.glBegin(GL2.GL_POLYGON);
                    gl.glVertex3d(-0.4, -0.45, 0.32);
                    gl.glVertex3d(-0.4, -0.30, 0.32);
                    gl.glVertex3d(0.4, -0.30, 0.32);
                    gl.glVertex3d(0.4, -0.45, 0.32);
                gl.glEnd();
            gl.glPopMatrix();
        }
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.WHITE);
            textRenderer.draw("Jogo Pausado", 520, 600);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.WHITE);
            textRenderer.draw("Voltar ao Jogo", 520, 430);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.WHITE);
            textRenderer.draw("Menu", 520, 360);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.WHITE);
            textRenderer.draw("Sair do Jogo", 520, 290);
        textRenderer.endRendering();
    }
    
    // Metodo que desenha o menu de sair do jogo
    public void desenhaSairMenu(GL2 gl){
        gl.glColor3d(1, 1, 1);
        gl.glPushMatrix();
            gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex3d(-0.405, -0.305, 0.33);
                gl.glVertex3d(-0.405, 0.405, 0.33);
                gl.glVertex3d(0.405, 0.405, 0.33);
                gl.glVertex3d(0.405, -0.305, 0.33);
            gl.glEnd();
        gl.glPopMatrix();
        gl.glColor3d(0.098, 0.102, 0.341);
        gl.glPushMatrix();
            gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex3d(-0.4, -0.3, 0.34);
                gl.glVertex3d(-0.4, 0.4, 0.34);
                gl.glVertex3d(0.4, 0.4, 0.34);
                gl.glVertex3d(0.4, -0.3, 0.34);
            gl.glEnd();
        gl.glPopMatrix();
        if(opSimSair){
            gl.glColor3d(0.353, 0.004, 0.008);
            gl.glPushMatrix();
                gl.glBegin(GL2.GL_POLYGON);
                    gl.glVertex3d(-0.4, -0.15, 0.35);
                    gl.glVertex3d(-0.4, 0, 0.35);
                    gl.glVertex3d(0.4, 0, 0.35);
                    gl.glVertex3d(0.4, -0.15, 0.35);
                gl.glEnd();
            gl.glPopMatrix();
        }
        if(opNaoSair){
            gl.glColor3d(0.353, 0.004, 0.008);
            gl.glPushMatrix();
                gl.glBegin(GL2.GL_POLYGON);
                    gl.glVertex3d(-0.4, -0.30, 0.35);
                    gl.glVertex3d(-0.4, -0.15, 0.35);
                    gl.glVertex3d(0.4, -0.15, 0.35);
                    gl.glVertex3d(0.4, -0.30, 0.35);
                gl.glEnd();
            gl.glPopMatrix();
        }
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.WHITE);
            textRenderer.draw("Deseja Sair?", 520, 600);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.WHITE);
            textRenderer.draw("Sim", 520, 430);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.WHITE);
            textRenderer.draw("Não", 520, 360);
        textRenderer.endRendering();
    }
    
    // Metodo que desenha o menu de morte de jogo
    public void desenhaMorteMenu(GL2 gl){
        gl.glColor3d(1, 1, 1);
        gl.glPushMatrix();
            gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex3d(-0.405, -0.455, 0.3);
                gl.glVertex3d(-0.405, 0.505, 0.3);
                gl.glVertex3d(0.405, 0.505, 0.3);
                gl.glVertex3d(0.405, -0.455, 0.3);
            gl.glEnd();
        gl.glPopMatrix();
        gl.glColor3d(0.098, 0.102, 0.341);
        gl.glPushMatrix();
            gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex3d(-0.4, -0.45, 0.31);
                gl.glVertex3d(-0.4, 0.5, 0.31);
                gl.glVertex3d(0.4, 0.5, 0.31);
                gl.glVertex3d(0.4, -0.45, 0.31);
            gl.glEnd();
        gl.glPopMatrix();
        if(opTentarMorte){
            gl.glColor3d(0.353, 0.004, 0.008);
            gl.glPushMatrix();
                gl.glBegin(GL2.GL_POLYGON);
                    gl.glVertex3d(-0.4, -0.15, 0.32);
                    gl.glVertex3d(-0.4, 0, 0.32);
                    gl.glVertex3d(0.4, 0, 0.32);
                    gl.glVertex3d(0.4, -0.15, 0.32);
                gl.glEnd();
            gl.glPopMatrix();
        }
        if(opMenuMorte){
            gl.glColor3d(0.353, 0.004, 0.008);
            gl.glPushMatrix();
                gl.glBegin(GL2.GL_POLYGON);
                    gl.glVertex3d(-0.4, -0.30, 0.32);
                    gl.glVertex3d(-0.4, -0.15, 0.32);
                    gl.glVertex3d(0.4, -0.15, 0.32);
                    gl.glVertex3d(0.4, -0.30, 0.32);
                gl.glEnd();
            gl.glPopMatrix();
        }
        if(opSairMorte){
            gl.glColor3d(0.353, 0.004, 0.008);
            gl.glPushMatrix();
                gl.glBegin(GL2.GL_POLYGON);
                    gl.glVertex3d(-0.4, -0.45, 0.32);
                    gl.glVertex3d(-0.4, -0.30, 0.32);
                    gl.glVertex3d(0.4, -0.30, 0.32);
                    gl.glVertex3d(0.4, -0.45, 0.32);
                gl.glEnd();
            gl.glPopMatrix();
        }
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.WHITE);
            textRenderer.draw("Você Morreu!", 520, 650);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.WHITE);
            textRenderer.draw("Pontuação: " + pontuacao, 520, 600);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.WHITE);
            textRenderer.draw("Tentar de novo", 520, 430);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.WHITE);
            textRenderer.draw("Menu", 520, 360);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.WHITE);
            textRenderer.draw("Sair do Jogo", 520, 290);
        textRenderer.endRendering();
    }
    
    // Metodo que define o que a tecla da setinha para cima irá fazer
    public void teclaCima(){
        if(telaMenu){
            if(sairMenu){
                if(opSimSair){
                    opSimSair = false;
                    opNaoSair = true;
                }else if(opNaoSair){
                    opNaoSair = false;
                    opSimSair = true;
                }
            }else{
                if(opJogarMenu){
                    opJogarMenu = false;
                    opSairMenu = true;
                }else if(opControlesMenu){
                    opControlesMenu = false;
                    opJogarMenu = true;
                }else if(opCreditosMenu){
                    opCreditosMenu = false;
                    opControlesMenu = true;
                }else if(opSairMenu){
                    opSairMenu = false;
                    opCreditosMenu = true;
                }
            }
        }else if(telaJogo){
            if(sairMenu){
                if(opSimSair){
                    opSimSair = false;
                    opNaoSair = true;
                }else if(opNaoSair){
                    opNaoSair = false;
                    opSimSair = true;
                }
            }
            if((morteMenu)&&(!sairMenu)){
                if(opTentarMorte){
                    opTentarMorte = false;
                    opSairMorte = true;
                }else if(opMenuMorte){
                    opMenuMorte = false;
                    opTentarMorte = true;
                }else if(opSairMorte){
                    opSairMorte = false;
                    opMenuMorte = true;
                }
            }
            if((pausadoMenu)&&(!sairMenu)){
                if(opVoltarPause){
                    opVoltarPause = false;
                    opSairPause = true;
                }else if(opMenuPause){
                    opMenuPause = false;
                    opVoltarPause = true;
                }else if(opSairPause){
                    opSairPause = false;
                    opMenuPause = true;
                }
            }
        }
    }
    
    // Metodo que define o que a tecla da setinha para baixo irá fazer
    public void teclaBaixo(){
        if(telaMenu){
            if(sairMenu){
                if(opSimSair){
                    opSimSair = false;
                    opNaoSair = true;
                }else if(opNaoSair){
                    opNaoSair = false;
                    opSimSair = true;
                }
            }else{
                if(opJogarMenu){
                    opJogarMenu = false;
                    opControlesMenu = true;
                }else if(opControlesMenu){
                    opControlesMenu = false;
                    opCreditosMenu = true;
                }else if(opCreditosMenu){
                    opCreditosMenu = false;
                    opSairMenu = true;
                }else if(opSairMenu){
                    opSairMenu = false;
                    opJogarMenu = true;
                }
            }
        }else if(telaJogo){
            if(sairMenu){
                if(opSimSair){
                    opSimSair = false;
                    opNaoSair = true;
                }else if(opNaoSair){
                    opNaoSair = false;
                    opSimSair = true;
                }
            }
            if((morteMenu)&&(!sairMenu)){
                if(opTentarMorte){
                    opTentarMorte = false;
                    opMenuMorte = true;
                }else if(opMenuMorte){
                    opMenuMorte = false;
                    opSairMorte = true;
                }else if(opSairMorte){
                    opSairMorte = false;
                    opTentarMorte = true;
                }
            }
            if((pausadoMenu)&&(!sairMenu)){
                if(opVoltarPause){
                    opVoltarPause = false;
                    opMenuPause = true;
                }else if(opMenuPause){
                    opMenuPause = false;
                    opSairPause = true;
                }else if(opSairPause){
                    opSairPause = false;
                    opVoltarPause = true;
                }
            }
        }
    }
    
    // Metodo que define o que a tecla espaço irá fazer
    public void teclaSpaco(){
        if(telaMenu){
            if(sairMenu){
                if(opSimSair){
                    System.exit(0);
                }else if(opNaoSair){
                    fecharSairMenu();
                }
            }else{
                if(opJogarMenu){
                    trocarTela(2);
                }else if(opControlesMenu){
                    trocarTela(3);
                }else if(opCreditosMenu){
                    trocarTela(4);
                }else if(opSairMenu){
                    chamarSairMenu();
                }
            }
        }else if(telaJogo){
            if((!pausadoMenu)&&(!morteMenu)){
                ovni.pular();
            }
            if(morteMenu){
                if(sairMenu){
                    if(opSimSair){
                        System.exit(0);
                    }else if(opNaoSair){
                        fecharSairMenu();
                    }
                }else{
                    if(opTentarMorte){
                        iniciarJogo();
                    }else if(opMenuMorte){
                        trocarTela(1);
                    }else if(opSairMorte){
                        chamarSairMenu();
                    }
                }
            }
            if(pausadoMenu){
                if(sairMenu){
                    if(opSimSair){
                        System.exit(0);
                    }else if(opNaoSair){
                        fecharSairMenu();
                    }
                }else{
                    if(opVoltarPause){
                        fecharPausadoMenu();
                    }else if(opMenuPause){
                        trocarTela(1);
                    }else if(opSairPause){
                        chamarSairMenu();
                    }
                }
            }
        }
    }
    
    // Metodo que define o que a tecla "Esc" irá fazer
    public void teclaEsc(){
        if(telaMenu){
            if(sairMenu){
                fecharSairMenu();
            }else{
                chamarSairMenu();
            }
        }else if(telaJogo){
            if(morteMenu){
                if(sairMenu){
                    fecharSairMenu();
                }else{
                    iniciarJogo();
                }
            }else if(pausadoMenu){
                if(sairMenu){
                    fecharSairMenu();
                }else{
                    fecharPausadoMenu();
                }
            }else if((!pausadoMenu)&&(!morteMenu)){
                chamarPausadoMenu();
            }
        }else if(telaControles){
            trocarTela(1);
        }else if(telaCreditos){
            trocarTela(1);
        }
        
    }
    
    // Metodo que chama o menu de sair do jogo
    public void chamarSairMenu(){
        sairMenu = true;
        opSimSair = false;
        opNaoSair = true;
    }
    
    // Metodo que fecha o menu de sair do jogo
    public void fecharSairMenu(){
        sairMenu = false;
    }
    
    // Metodo que chama o menu de pause dentro do jogo
    public void chamarPausadoMenu(){
        pausadoMenu = true;
        opVoltarPause = true;
        opMenuPause = false;
        opSairPause = false;
    }
    
    // Metodo que fecha o menu de pause
    public void fecharPausadoMenu(){
        pausadoMenu = false;
    }
    
    // Metodo que chama o menu de morte dentro do jogo
    public void chamarMorteMenu(){
        morteMenu = true;
        opTentarMorte = true;
        opMenuMorte = false;
        opSairMorte = false;
    }
    
    // Metodo que fecha o menu de morte
    public void fecharMorteMenu(){
        morteMenu = false;
    }
    
    // Metodo que inicia as variáveis do jogo
    public void iniciarJogo(){
        ovni = new OvniGato();
        pontuacao = 0;
        listaAsteroide = new ArrayList<Asteroide>();
        pausadoMenu = sairMenu = morteMenu = false;
        opVoltarPause = opMenuPause = opSairPause = false;
        opSimSair = opNaoSair = false;
        opTentarMorte = opMenuMorte = opSairMorte = false;
    }
    
    // Metodo para trocar as telas do jogo
    public void trocarTela(int codTela){
        switch (codTela) {
            case 1 -> {
                telaMenu = true;
                telaJogo = false;
                telaCreditos = false;
                telaControles = false;
                opJogarMenu = true;
                opControlesMenu = false;
                opCreditosMenu = false;
                opSairMenu = false;
            }
            case 2 -> {
                telaMenu = false;
                telaJogo = true;
                telaCreditos = false;
                telaControles = false;
                iniciarJogo();
            }
            case 3 -> {
                telaMenu = false;
                telaJogo = false;
                telaControles = true;
                telaCreditos = false;
            }
            case 4 -> {
                telaMenu = false;
                telaJogo = false;
                telaControles = false;
                telaCreditos = true;
            }
            default -> System.out.println("Tela invalida");
        }
    }
    
    // Metodo que configura e habilita a iluminação
    public void iluminacao(GL2 gl){
        float luzAmbiente[] = {0.2f, 0.2f, 0.2f, 1f};
        float posicaoLuz[] = {0.0f, 50.0f, 50.0f, 0.0f};
        float especularidade[] = {1.0f, 1.0f, 1.0f, 1.0f};
        int especMaterial = 60;
        
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, especularidade, 0);
        gl.glMateriali(GL2.GL_FRONT, GL2.GL_SHININESS, especMaterial);
        
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, luzAmbiente, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, luzAmbiente, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, posicaoLuz, 0);
        
        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glShadeModel(GL2.GL_SMOOTH);
    }
}
