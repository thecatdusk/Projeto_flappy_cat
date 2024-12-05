package cena;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import textura.Textura;

public class OvniGato {
    // Variaveis de tamanho, posição e velocidade de movimentação do Ovni
    private float alturaBase, larguraBase, alturaCapsula, larguraCapsula, yOvni, xOvni, velocidade;
    
    // Variaveis de posição das luzes do Ovni e da animação de rotação
    private double anguloLuzes, velRotacao, anguloRotacao;
    
    // Constande da Gravidade
    private final float GRAVIDADE = -3.5f;
    
    // Construtor do Ovni
    public OvniGato(){
        alturaBase = 0.08f;
        larguraBase = 0.15f;
        alturaCapsula = 0.12f;
        larguraCapsula = 0.1f;
        yOvni = 0f;
        xOvni = -1.4f;
        velocidade = 0f;
        velRotacao = 90;
        anguloRotacao = 0;
    }
    
    // Método para desenhar o Ovni
    public void desenhaOvni(GL2 gl, GLUT glut, Textura textura, String texturaMetalOvni, String texturaVidroOvni, float limite){
        anguloLuzes = 0;
        
        //Ovni
        gl.glPushMatrix();
            gl.glTranslated(xOvni, yOvni, 0);
            gl.glRotated(anguloRotacao, 0, 1, 0);
            gl.glColor3d(0.7, 0.7, 0.7);
            // Carcaça do Ovni
            gl.glPushMatrix();
                //Configuração e aplicação de textura na carcaça
                gl.glMatrixMode(GL2.GL_TEXTURE);
                    gl.glLoadIdentity();                      
                    gl.glScalef(limite/textura.getWidth(), limite/textura.getHeight(), limite);           
                gl.glMatrixMode(GL2.GL_MODELVIEW);
                textura.setAutomatica(true);
                textura.setFiltro(GL2.GL_NEAREST);
                textura.setModo(GL2.GL_DECAL);
                textura.setWrap(GL2.GL_REPEAT);
                textura.gerarTextura(gl, texturaMetalOvni, 0);
                // Definição da posição, rotação e da forma da carcaça
                gl.glTranslated(0, 0.04, 0);
                gl.glRotated(90, 1, 0, 0);
                glut.glutSolidTorus(0.04, 0.11, 50, 50);
                textura.desabilitarTextura(gl, 0);
            gl.glPopMatrix();
            gl.glColor3d(0.3, 1, 0.3);
            for(int i = 0;i < 12;i++){ // Desenha das luzes do Ovni
                gl.glPushMatrix();
                    gl.glTranslated(0.145*Math.sin(anguloLuzes), 0.04, 0.145*Math.cos(anguloLuzes));
                    gl.glRotated(0, 0, 0, 0);
                    glut.glutSolidSphere(0.015, 50, 50);
                gl.glPopMatrix();
                anguloLuzes += (2*Math.PI)/12;
            }
            gl.glColor3d(0.7, 0.7, 0.7);
            gl.glPushMatrix(); // Desenho e aplicação de textura na base do Ovni
                gl.glMatrixMode(GL2.GL_TEXTURE);
                    gl.glLoadIdentity();                      
                    gl.glScalef(limite/textura.getWidth(), limite/textura.getHeight(), limite);           
                gl.glMatrixMode(GL2.GL_MODELVIEW);
                textura.setAutomatica(true);
                textura.setFiltro(GL2.GL_NEAREST);
                textura.setModo(GL2.GL_DECAL);
                textura.setWrap(GL2.GL_REPEAT);
                textura.gerarTextura(gl, texturaMetalOvni, 0);
                gl.glTranslated(0, 0.001, 0);
                gl.glRotated(90, 1, 0, 0);
                glut.glutSolidCylinder(0.105, 0.01, 50, 1);
                textura.desabilitarTextura(gl, 0);
            gl.glPopMatrix(); 
            gl.glColor3d(0.4, 0.4, 0.4);
            gl.glPushMatrix(); // Desenho da Capsula do Ovni
                gl.glTranslated(0, 0.09, 0);
                gl.glRotated(90, 1, 0, 0);
                glut.glutSolidSphere(0.1, 50, 50);
            gl.glPopMatrix();
        gl.glPopMatrix();
    }
    
    // Método que adiciona a gravidade à velocidade
    public void calcularGravidade(double deltaT){
        velocidade += GRAVIDADE * deltaT;
    }
    
    // Método de pulo
    public void pular(){
        velocidade = 1.5f;
    }
    
    // Método que aplica o movimento
    public void moverOvni(double deltaT){
        yOvni += velocidade * deltaT;
    }
    
    // Método que verifica colisão com os asteroides
    public boolean verificarColisao(double posAsteroideX, double posAsteroideY, double tamanhoAsteroide){
        boolean colidiu = false;
        if ((posAsteroideX - tamanhoAsteroide < xOvni + larguraBase)&&(posAsteroideX + tamanhoAsteroide > xOvni - larguraBase)&&(posAsteroideY - tamanhoAsteroide < yOvni + alturaBase)&&(posAsteroideY + tamanhoAsteroide > yOvni)){
            colidiu = true;
        }else if((posAsteroideX - tamanhoAsteroide < xOvni + larguraCapsula)&& (posAsteroideX + tamanhoAsteroide > xOvni - larguraCapsula)&&(posAsteroideY - tamanhoAsteroide < yOvni + alturaBase + alturaCapsula)&&(posAsteroideY + tamanhoAsteroide > yOvni + alturaBase)){
            colidiu = true;
        }
        return colidiu;
    }
    
    // Verificação de colisão com as bordas da tela
    public boolean colisaoBorda(){
        boolean colidiu = false;
        if ((yOvni + alturaBase + alturaCapsula > 1)||(yOvni < -1)){
            colidiu = true;
        }
        return colidiu;
    }
    
    // Metodo que verifica se ponto foi feito
    public boolean verificarPonto(double asteroideX, boolean valePonto){
        boolean fezPonto = false;
        if((xOvni >= asteroideX)&&(valePonto)){
            fezPonto = true;
        }
        return fezPonto;
    }
    
    // Metodo que aplica a animação de rotação
    public void aplicaAnimacao(double deltaT){
        anguloRotacao += velRotacao * deltaT;
    }
}
