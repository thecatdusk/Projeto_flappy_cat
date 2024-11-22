package cena;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import java.util.ArrayList;
import com.jogamp.opengl.util.awt.TextRenderer; //ADICIONADO
import java.awt.Color; //ADICIONADO
import java.awt.Font; //ADICIONADO
/**
 *
 * @author Kakugawa
 */
public class Cena implements GLEventListener{    
    private float xMin, xMax, yMin, yMax, zMin, zMax;    
    GLU glu;
    OvniGato ovni;
    private long lastFrame;
    private double deltaT, timerAsteroide;
    private boolean colidiu, pausado;
    ArrayList<Asteroide> listaAsteroide = new ArrayList<Asteroide>();
    private TextRenderer textRenderer;
    private int pontuacao;
    
    @Override
    public void init(GLAutoDrawable drawable) {
        //dados iniciais da cena
        glu = new GLU();
        //Estabelece as coordenadas do SRU (Sistema de Referencia do Universo)
        xMin = yMin = zMin = -1;
        xMax = yMax = zMax = 1;
        
        
        textRenderer = new TextRenderer(new Font("Comic Sans MS Negrito", Font.PLAIN, 38));
        
        //Inicia as variáveis de jogo
        lastFrame = 0;
        deltaT = 0;
        ovni = new OvniGato();
        colidiu = false;
        pontuacao = 0;
        pausado = false;
    }

    @Override
    public void display(GLAutoDrawable drawable) {  
        //obtem o contexto Opengl
        GL2 gl = drawable.getGL().getGL2();
        //ADICIONADO (Habilita o buffer de profundidade)
        //gl.glEnable(GL2.GL_DEPTH_TEST);
        //define a cor da janela (R, G, G, alpha)
        gl.glClearColor(0, 0, 0, 1);        
        //limpa a janela com a cor especificada
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);       
        gl.glLoadIdentity(); //lê a matriz identidade
        
        // Calculo do delta
        calcDeltaT();
        
        // Timer de spawn de asteroides
        timerSpawn();
        
        // Calculo de fisica
        calcFisica();
        
        // Desenho da cena
        desenhaCena(gl);
        
        // Lida com derrota
        if(colidiu){
            ovniBateu();
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
    
    public void ovniPular(){
        if(!pausado){
            ovni.pular();
        }
    }
    
    public void calcFisica(){
        if(!pausado){
            ovni.calcularGravidade(deltaT);
            ovni.moverOvni(deltaT);
            for(int i = 0; i < listaAsteroide.size(); i++){
                listaAsteroide.get(i).moverAsteroide(deltaT);
                if(listaAsteroide.get(i).getPosicaoX() <= -2.5){
                    listaAsteroide.remove(i);
                }
                if (ovni.verificarColisao(listaAsteroide.get(i).getPosicaoX(), listaAsteroide.get(i).getPosicaoY(), listaAsteroide.get(i).getTamanho())){
                    colidiu = true;
                }
                if (ovni.verificarPonto(listaAsteroide.get(i).getPosicaoX(), listaAsteroide.get(i).getValePonto())){
                    fezPonto();
                    listaAsteroide.get(i).fezPonto();
                }
            }
            if (ovni.colisaoBorda()){
                colidiu = true;
            }
        }
        
    }
    
    public void desenhaCena(GL2 gl){
        ovni.desenhaOvni(gl);
        for(int i = 0; i < listaAsteroide.size(); i++){
            listaAsteroide.get(i).desenhaAsteroide(gl);
        }
        desenhaPontuacao();
        if(pausado){
            desenhaTelaPause(gl);
        }
    }
    
    public void calcDeltaT(){
        if(lastFrame == 0){
            lastFrame = System.currentTimeMillis();
        }else{
            deltaT = System.currentTimeMillis() - lastFrame;
            deltaT /= 1000;
            lastFrame = System.currentTimeMillis();
        }
    }
    
    public void timerSpawn(){
        if(!pausado){
            timerAsteroide += deltaT;
            if (timerAsteroide >= 2.25){
                spawnAsteroide();
                timerAsteroide = 0.0;
            }
        }
    }
    
    public void spawnAsteroide(){
        listaAsteroide.add(new Asteroide());
    }
    
    public void ovniBateu(){
        System.out.println("Bateu!");
    }
    
    public void fezPonto(){
        pontuacao++;
    }
    
    public void desenhaPontuacao(){
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.BLUE);
            textRenderer.draw(pontuacao+"", 620, 900);
        textRenderer.endRendering();
    }
    
    public void pausar(){
        pausado = !pausado;
    }
    
    public void desenhaTelaPause(GL2 gl){
        gl.glColor3d(1, 1, 0);
        gl.glPushMatrix();
            gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex2d(-0.4, -0.5);
                gl.glVertex2d(-0.4, 0.5);
                gl.glVertex2d(0.4, 0.5);
                gl.glVertex2d(0.4, -0.5);
            gl.glEnd();
        gl.glPopMatrix();
        gl.glColor3d(0, 1, 1);
        gl.glPushMatrix();
            gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex2d(-0.35, -0.15);
                gl.glVertex2d(-0.35, 0);
                gl.glVertex2d(0.35, 0);
                gl.glVertex2d(0.35, -0.15);
            gl.glEnd();
        gl.glPopMatrix();
        gl.glColor3d(0, 1, 1);
        gl.glPushMatrix();
            gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex2d(-0.35, -0.35);
                gl.glVertex2d(-0.35, -0.20);
                gl.glVertex2d(0.35, -0.20);
                gl.glVertex2d(0.35, -0.35);
            gl.glEnd();
        gl.glPopMatrix();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.BLUE);
            textRenderer.draw("Jogo Pausado", 520, 600);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.BLUE);
            textRenderer.draw("Menu", 520, 430);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.BLUE);
            textRenderer.draw("Sair do Jogo", 520, 335);
        textRenderer.endRendering();
    }
    
    public void click(float mouseX, float mouseY){
        if(pausado){
            System.out.println("X: "+mouseX+", Y:"+mouseY);
        }
    }
}
