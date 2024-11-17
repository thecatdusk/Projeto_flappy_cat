package cena;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;

public class OvniGato {
    private float alturaBase, larguraBase, alturaCapsula, larguraCapsula, yOvni, xOvni, velocidade;
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
    }
    
    // Método para desenhar o Ovni
    public void desenhaOvni(GL2 gl){
        gl.glColor3d(1, 1, 1);
        gl.glBegin(GL2.GL_POLYGON);
            gl.glVertex2d(xOvni - larguraBase, yOvni);
            gl.glVertex2d(xOvni - larguraBase, yOvni + alturaBase);
            gl.glVertex2d(xOvni + larguraBase, yOvni + alturaBase);
            gl.glVertex2d(xOvni + larguraBase, yOvni);
        gl.glEnd();
        gl.glBegin(GL2.GL_POLYGON);
            gl.glVertex2d(xOvni - larguraCapsula, yOvni + alturaBase);
            gl.glVertex2d(xOvni - larguraCapsula, yOvni + alturaBase + alturaCapsula);
            gl.glVertex2d(xOvni + larguraCapsula, yOvni + alturaBase + alturaCapsula);
            gl.glVertex2d(xOvni + larguraCapsula, yOvni + alturaBase);
        gl.glEnd();
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
    
    // Método que verifica colisão
    public boolean verificarColisao(double posAsteroideX, double posAsteroideY, double tamanhoAsteroide){
        boolean colidiu = false;
        if ((posAsteroideX - tamanhoAsteroide < xOvni + larguraBase)&&(posAsteroideX + tamanhoAsteroide > xOvni - larguraBase)&&(posAsteroideY - tamanhoAsteroide < yOvni + alturaBase)&&(posAsteroideY + tamanhoAsteroide > yOvni)){
            colidiu = true;
        }else if((posAsteroideX - tamanhoAsteroide < xOvni + larguraCapsula)&& (posAsteroideX + tamanhoAsteroide > xOvni - larguraCapsula)&&(posAsteroideY - tamanhoAsteroide < yOvni + alturaBase + alturaCapsula)&&(posAsteroideY + tamanhoAsteroide > yOvni + alturaBase)){
            colidiu = true;
        }
        return colidiu;
    }
}
