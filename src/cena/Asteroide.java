package cena;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;

public class Asteroide {
    private double posicaoX, posicaoY, tamanho;
    private final float VELOCIDADE = -0.75f;
    private boolean valePonto;
    
    public Asteroide(){
        posicaoX = 2;
        posicaoY = (Math.random()*1.33) - 0.66;
        tamanho = ((Math.random()/3) + 0.66)/2;
        valePonto = true;
        //System.out.println(tamanho + ", " + posicaoY);
    }
    
    public void desenhaAsteroide(GL2 gl){
        gl.glColor3d(1, 0, 0);
        gl.glBegin(GL2.GL_POLYGON);
            gl.glVertex2d(posicaoX - tamanho, posicaoY - tamanho);
            gl.glVertex2d(posicaoX - tamanho, posicaoY + tamanho);
            gl.glVertex2d(posicaoX + tamanho, posicaoY + tamanho);
            gl.glVertex2d(posicaoX + tamanho, posicaoY - tamanho);
        gl.glEnd();
    }
    
    public void moverAsteroide(double deltaT){
        posicaoX += VELOCIDADE * deltaT;
    }
    
    public double getTamanho(){
        return tamanho;
    }
    
    public double getPosicaoX(){
        return posicaoX;
    }
    
    public double getPosicaoY(){
        return posicaoY;
    }
    
    public boolean getValePonto(){
        return valePonto;
    }
    
    public void fezPonto(){
        valePonto = false;
    }
}
