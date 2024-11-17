package cena;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import java.util.ArrayList;
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
    private boolean colidiu;
    ArrayList<Asteroide> listaAsteroide = new ArrayList<Asteroide>();
    
    @Override
    public void init(GLAutoDrawable drawable) {
        //dados iniciais da cena
        glu = new GLU();
        //Estabelece as coordenadas do SRU (Sistema de Referencia do Universo)
        xMin = yMin = zMin = -1;
        xMax = yMax = zMax = 1;
        
        //Inicia as variáveis de jogo
        lastFrame = 0;
        deltaT = 0;
        ovni = new OvniGato();
        
    }

    @Override
    public void display(GLAutoDrawable drawable) {  
        //obtem o contexto Opengl
        GL2 gl = drawable.getGL().getGL2();                
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
        
        // desenho da cena
        desenhaCena(gl);
        
        
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
        ovni.pular();
    }
    
    public void calcFisica(){
        ovni.calcularGravidade(deltaT);
        ovni.moverOvni(deltaT);
        for(int i = 0; i < listaAsteroide.size(); i++){
            listaAsteroide.get(i).moverAsteroide(deltaT);
            if(listaAsteroide.get(i).getPosicaoX() <= -2.5){
                listaAsteroide.remove(i);
            }
        }
        for(int i = 0; i < listaAsteroide.size(); i++){
            colidiu = ovni.verificarColisao(listaAsteroide.get(i).getPosicaoX(), listaAsteroide.get(i).getPosicaoY(), listaAsteroide.get(i).getTamanho());
            if (colidiu){
                ovniBateu();
            }
        }
    }
    
    public void desenhaCena(GL2 gl){
        ovni.desenhaOvni(gl);
        for(int i = 0; i < listaAsteroide.size(); i++){
            listaAsteroide.get(i).desenhaAsteroide(gl);
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
        timerAsteroide += deltaT;
        if (timerAsteroide >= 1.25){
            spawnAsteroide();
            timerAsteroide = 0.0;
        }
    }
    
    public void spawnAsteroide(){
        listaAsteroide.add(new Asteroide());
    }
    
    public void ovniBateu(){
        System.out.println("Bateu!");
    }
}
