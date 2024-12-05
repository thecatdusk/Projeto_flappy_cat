package cena;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import textura.Textura;

public class Asteroide {
    // Variaveis de posição e tamanho do asteroide
    private double posicaoX, posicaoY, tamanho;
    
    // Constante de velocidade
    private final float VELOCIDADE = -0.75f;
    
    // Variavel que determina se o asteroide vale ponto ou não
    private boolean valePonto;
    
    // Variavel e Arrays que determinam a forma do asteroide
    private int contadorCanto;
    private double rotacaoFragmentoCantoX[] = new double[4];
    private double rotacaoFragmentoCantoY[] = new double[4];
    private double rotacaoFragmentoCantoZ[] = new double[4];
    private double posFragmentoX[] = new double[10];
    private double posFragmentoY[] = new double[10];
    private double rotacaoFragmentoX[] = new double[10];
    private double rotacaoFragmentoY[] = new double[10];
    private double rotacaoFragmentoZ[] = new double[10];
    
    public Asteroide(){ // Construtor do Asteroide
        // Definição da posição inicial do asteroide, tamanho e que vale ponto
        posicaoX = 2;
        posicaoY = (Math.random()*1.33) - 0.66; // Posição Y aleatória
        tamanho = 0.375;
        valePonto = true;
        
        // Randomização da rotação dos fragmentos do canto do Asteroide
        for(int i = 0; i < 4; i++){
            rotacaoFragmentoCantoX[i] = Math.random()*360;
            rotacaoFragmentoCantoY[i] = Math.random()*360;
            rotacaoFragmentoCantoZ[i] = Math.random()*360;
        }
        
        // Randomização da rotação e da posição dos Fragmentos centrais do asteroide
        for(int i = 0; i < 10; i++){
            posFragmentoX[i] = (Math.random()/2)-0.25;
            posFragmentoY[i] = (Math.random()/2)-0.25;
            rotacaoFragmentoX[i] = Math.random()*360;
            rotacaoFragmentoY[i] = Math.random()*360;
            rotacaoFragmentoZ[i] = Math.random()*360;
        }
    }
    
    // Metodo que desenha o asteroide
    public void desenhaAsteroide(GL2 gl, GLUT glut, Textura textura, String texturaAsteroide, float limite){
        contadorCanto = 0; // Inicialização do Contador de cantos
        gl.glColor3d(0.2, 0.2, 0.2);
        //Estrutura de repetição que seleciona, define a rotação de cada canto, sua posição, aplica sua textura e o desenha
        for(int i = 0; i < 2; i++){ 
            for(int j = 0; j < 2; j++){
                gl.glPushMatrix();
                    gl.glMatrixMode(GL2.GL_TEXTURE);
                        gl.glLoadIdentity();                      
                        gl.glScalef(limite/textura.getWidth(), limite/textura.getHeight(), limite);           
                    gl.glMatrixMode(GL2.GL_MODELVIEW);
                    textura.setAutomatica(true);
                    textura.setFiltro(GL2.GL_NEAREST);
                    textura.setModo(GL2.GL_DECAL);
                    textura.setWrap(GL2.GL_REPEAT);
                    textura.gerarTextura(gl, texturaAsteroide, 0);
                    gl.glTranslated(posicaoX + (-0.25 + (0.25 * 2 * i)), posicaoY +(-0.25 + (0.25 * 2 * j)), 0);
                    gl.glRotated(rotacaoFragmentoCantoX[contadorCanto], 1, 0, 0);
                    gl.glRotated(rotacaoFragmentoCantoY[contadorCanto], 0, 1, 0);
                    gl.glRotated(rotacaoFragmentoCantoZ[contadorCanto], 0, 0, 1);
                    glut.glutSolidCube(0.25f);
                    textura.desabilitarTextura(gl, 0);
                gl.glPopMatrix();
                contadorCanto++;
            }
        }
        // Estrutura de repetição que determina a posição, a rotação, aplica a textura e desenha os demais fragmentos
        for(int i = 0; i < 10; i++){
            gl.glPushMatrix();
                gl.glMatrixMode(GL2.GL_TEXTURE);
                    gl.glLoadIdentity();                      
                    gl.glScalef(limite/textura.getWidth(), limite/textura.getHeight(), limite);           
                gl.glMatrixMode(GL2.GL_MODELVIEW);
                textura.setAutomatica(true);
                textura.setFiltro(GL2.GL_NEAREST);
                textura.setModo(GL2.GL_DECAL);
                textura.setWrap(GL2.GL_REPEAT);
                textura.gerarTextura(gl, texturaAsteroide, 0);
                gl.glTranslated(posicaoX + posFragmentoX[i], posicaoY + posFragmentoY[i], 0);
                gl.glRotated(rotacaoFragmentoX[i], 1, 0, 0);
                gl.glRotated(rotacaoFragmentoY[i], 0, 1, 0);
                gl.glRotated(rotacaoFragmentoZ[i], 0, 0, 1);
                glut.glutSolidCube(0.25f);
                textura.desabilitarTextura(gl, 0);
            gl.glPopMatrix();
        }
        
    }
    
    // Metodo que aplica o movimento no asteroide
    public void moverAsteroide(double deltaT){
        posicaoX += VELOCIDADE * deltaT;
    }
    
    // Metodo get para pegar o tamanho do asteroide
    public double getTamanho(){
        return tamanho;
    }
    
    // Metodo get para pegar a posição do eixo X do Asteroide
    public double getPosicaoX(){
        return posicaoX;
    }
    
    // Metodo get para pegar a posição do eixo Y do Asteroide
    public double getPosicaoY(){
        return posicaoY;
    }
    
    // Metodo get para verificar se o asteroide ainda vale pontos
    public boolean getValePonto(){
        return valePonto;
    }
    
    // Metodo que remove a propriedade de fazer ponto do asteroide
    public void fezPonto(){
        valePonto = false;
    }
}
