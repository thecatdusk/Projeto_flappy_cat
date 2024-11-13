package cena;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;

public class OvniGato {
    private float alturaBase, larguraBase, alturaCapsula, larguraCapsula, yOvni, xOvni, velocidade;
    private Float GRAVIDADE = -3.5f;
    public OvniGato(){
        alturaBase = 0.08f;
        larguraBase = 0.15f;
        alturaCapsula = 0.12f;
        larguraCapsula = 0.1f;
        yOvni = 0f;
        xOvni = -1.4f;
        velocidade = 0f;
    }
    
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
    
    public void calcularGravidade(double deltaT){
        velocidade += GRAVIDADE * deltaT;
    }
    
    public void pular(){
        velocidade = 1.5f;
    }
    
    public void moverOvni(double deltaT){
        yOvni += velocidade * deltaT;
    }
}
