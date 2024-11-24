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
    private boolean pausadoMenu, sairMenu, morteMenu, opVoltarPause, opMenuPause, opSairPause, opSimSair, opNaoSair, opTentarMorte, opMenuMorte, opSairMorte, telaJogo, telaMenu, telaControles, telaCreditos, opJogarMenu, opControlesMenu, opCreditosMenu, opSairMenu;
    ArrayList<Asteroide> listaAsteroide;
    private TextRenderer textRenderer;
    private int pontuacao;
    
    @Override
    public void init(GLAutoDrawable drawable) {
        //dados iniciais da cena
        glu = new GLU();
        //Estabelece as coordenadas do SRU (Sistema de Referencia do Universo)
        xMin = yMin = zMin = -1;
        xMax = yMax = zMax = 1;
        
        
        textRenderer = new TextRenderer(new Font("Comic Sans MS Negrito", Font.PLAIN, 28));
        
        //Inicia as variáveis de jogo
        lastFrame = 0;
        deltaT = 0;
        trocarTela(1);
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
        
        if(telaJogo){
            timerSpawn();
            calcFisica();
            desenhaJogo(gl);
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
    
    
    
    public void calcFisica(){
        if((!pausadoMenu)&&(!morteMenu)){
            ovni.calcularGravidade(deltaT);
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
        if((!pausadoMenu)&&(!morteMenu)){
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
    
    public void desenhaMenu(GL2 gl){
        if(opJogarMenu){
            gl.glColor3d(1, 0, 0);
            gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex2d(-0.36, -0.16);
                gl.glVertex2d(-0.36, 0.01);
                gl.glVertex2d(0.36, 0.01);
                gl.glVertex2d(0.36, -0.16);
            gl.glEnd();
        }
        gl.glColor3d(0, 1, 1);
        gl.glBegin(GL2.GL_POLYGON);
            gl.glVertex2d(-0.35, -0.15);
            gl.glVertex2d(-0.35, 0);
            gl.glVertex2d(0.35, 0);
            gl.glVertex2d(0.35, -0.15);
        gl.glEnd();
        if(opControlesMenu){
            gl.glColor3d(1, 0, 0);
            gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex2d(-0.36, -0.36);
                gl.glVertex2d(-0.36, -0.19);
                gl.glVertex2d(0.36, -0.19);
                gl.glVertex2d(0.36, -0.36);
            gl.glEnd();
        }
        gl.glColor3d(0, 1, 1);
        gl.glBegin(GL2.GL_POLYGON);
            gl.glVertex2d(-0.35, -0.35);
            gl.glVertex2d(-0.35, -0.2);
            gl.glVertex2d(0.35, -0.2);
            gl.glVertex2d(0.35, -0.35);
        gl.glEnd();
        if(opCreditosMenu){
            gl.glColor3d(1, 0, 0);
            gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex2d(-0.36, -0.56);
                gl.glVertex2d(-0.36, -0.39);
                gl.glVertex2d(0.36, -0.39);
                gl.glVertex2d(0.36, -0.56);
            gl.glEnd();
        }
        gl.glColor3d(0, 1, 1);
        gl.glBegin(GL2.GL_POLYGON);
            gl.glVertex2d(-0.35, -0.55);
            gl.glVertex2d(-0.35, -0.4);
            gl.glVertex2d(0.35, -0.4);
            gl.glVertex2d(0.35, -0.55);
        gl.glEnd();
        if(opSairMenu){
            gl.glColor3d(1, 0, 0);
            gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex2d(-0.36, -0.76);
                gl.glVertex2d(-0.36, -0.59);
                gl.glVertex2d(0.36, -0.59);
                gl.glVertex2d(0.36, -0.76);
            gl.glEnd();
        }
        gl.glColor3d(0, 1, 1);
        gl.glBegin(GL2.GL_POLYGON);
            gl.glVertex2d(-0.35, -0.75);
            gl.glVertex2d(-0.35, -0.6);
            gl.glVertex2d(0.35, -0.6);
            gl.glVertex2d(0.35, -0.75);
        gl.glEnd();
        
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.BLUE);
            textRenderer.draw("Alien Cat", 520, 600);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.BLUE);
            textRenderer.draw("Jogar", 520, 430);
        textRenderer.endRendering(); 
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.BLUE);
            textRenderer.draw("Controles", 520, 335);
        textRenderer.endRendering(); 
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.BLUE);
            textRenderer.draw("Creditos", 520, 240);
        textRenderer.endRendering(); 
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.BLUE);
            textRenderer.draw("Sair", 520, 145);
        textRenderer.endRendering();
        
        if(sairMenu){
            desenhaSairMenu(gl);
        }
        
    }
    
    public void desenhaJogo(GL2 gl){
        ovni.desenhaOvni(gl);
        for(int i = 0; i < listaAsteroide.size(); i++){
            listaAsteroide.get(i).desenhaAsteroide(gl);
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
    
    public void desenhaControles(GL2 gl){
        gl.glColor3d(1, 1, 0);
        gl.glPushMatrix();
            gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex2d(-0.4, -0.5);
                gl.glVertex2d(-0.4, 0.5);
                gl.glVertex2d(0.4, 0.5);
                gl.glVertex2d(0.4, -0.5);
            gl.glEnd();
        gl.glPopMatrix();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.BLUE);
            textRenderer.draw("Controles", 520, 600);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.BLUE);
            textRenderer.draw("Espaço: Pular", 520, 550);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.BLUE);
            textRenderer.draw("Esc: Pausar Jogo", 520, 500);
        textRenderer.endRendering();
    }
    
    public void desenhaCreditos(GL2 gl){
        gl.glColor3d(1, 1, 0);
        gl.glPushMatrix();
            gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex2d(-0.4, -0.5);
                gl.glVertex2d(-0.4, 0.5);
                gl.glVertex2d(0.4, 0.5);
                gl.glVertex2d(0.4, -0.5);
            gl.glEnd();
        gl.glPopMatrix();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.BLUE);
            textRenderer.draw("Creditos", 520, 650);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.BLUE);
            textRenderer.draw("Desenvolvedores:", 520, 550);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.BLUE);
            textRenderer.draw("Matheus Martins", 520, 500);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.BLUE);
            textRenderer.draw("Garcia", 520, 450);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.BLUE);
            textRenderer.draw("Miguel", 520, 400);
        textRenderer.endRendering();
    }
    
    public void desenhaPontuacao(){
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.BLUE);
            textRenderer.draw(pontuacao+"", 620, 900);
        textRenderer.endRendering();
    }
    
    public void desenhaPausadoMenu(GL2 gl){
        gl.glColor3d(1, 1, 0);
        gl.glPushMatrix();
            gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex2d(-0.4, -0.6);
                gl.glVertex2d(-0.4, 0.5);
                gl.glVertex2d(0.4, 0.5);
                gl.glVertex2d(0.4, -0.6);
            gl.glEnd();
        gl.glPopMatrix();
        if(opVoltarPause){
            gl.glColor3d(1, 0, 0);
            gl.glPushMatrix();
                gl.glBegin(GL2.GL_POLYGON);
                    gl.glVertex2d(-0.36, -0.16);
                    gl.glVertex2d(-0.36, 0.01);
                    gl.glVertex2d(0.36, 0.01);
                    gl.glVertex2d(0.36, -0.16);
                gl.glEnd();
            gl.glPopMatrix();
        }
        gl.glColor3d(0, 1, 1);
        gl.glPushMatrix();
            gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex2d(-0.35, -0.15);
                gl.glVertex2d(-0.35, 0);
                gl.glVertex2d(0.35, 0);
                gl.glVertex2d(0.35, -0.15);
            gl.glEnd();
        gl.glPopMatrix();
        if(opMenuPause){
            gl.glColor3d(1, 0, 0);
            gl.glPushMatrix();
                gl.glBegin(GL2.GL_POLYGON);
                    gl.glVertex2d(-0.36, -0.36);
                    gl.glVertex2d(-0.36, -0.19);
                    gl.glVertex2d(0.36, -0.19);
                    gl.glVertex2d(0.36, -0.36);
                gl.glEnd();
            gl.glPopMatrix();
        }
        gl.glColor3d(0, 1, 1);
        gl.glPushMatrix();
            gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex2d(-0.35, -0.35);
                gl.glVertex2d(-0.35, -0.20);
                gl.glVertex2d(0.35, -0.20);
                gl.glVertex2d(0.35, -0.35);
            gl.glEnd();
        gl.glPopMatrix();
        if(opSairPause){
            gl.glColor3d(1, 0, 0);
            gl.glPushMatrix();
                gl.glBegin(GL2.GL_POLYGON);
                    gl.glVertex2d(-0.36, -0.56);
                    gl.glVertex2d(-0.36, -0.39);
                    gl.glVertex2d(0.36, -0.39);
                    gl.glVertex2d(0.36, -0.56);
                gl.glEnd();
            gl.glPopMatrix();
        }
        gl.glColor3d(0, 1, 1);
        gl.glPushMatrix();
            gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex2d(-0.35, -0.55);
                gl.glVertex2d(-0.35, -0.40);
                gl.glVertex2d(0.35, -0.40);
                gl.glVertex2d(0.35, -0.55);
            gl.glEnd();
        gl.glPopMatrix();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.BLUE);
            textRenderer.draw("Jogo Pausado", 520, 600);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.BLUE);
            textRenderer.draw("Voltar ao Jogo", 520, 430);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.BLUE);
            textRenderer.draw("Menu", 520, 335);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.BLUE);
            textRenderer.draw("Sair do Jogo", 520, 240);
        textRenderer.endRendering();
    }
    
    public void desenhaSairMenu(GL2 gl){
        gl.glColor3d(0, 1, 0);
        gl.glPushMatrix();
            gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex2d(-0.4, -0.5);
                gl.glVertex2d(-0.4, 0.4);
                gl.glVertex2d(0.4, 0.4);
                gl.glVertex2d(0.4, -0.5);
            gl.glEnd();
        gl.glPopMatrix();
        if(opSimSair){
            gl.glColor3d(1, 0, 0);
            gl.glPushMatrix();
                gl.glBegin(GL2.GL_POLYGON);
                    gl.glVertex2d(-0.36, -0.16);
                    gl.glVertex2d(-0.36, 0.01);
                    gl.glVertex2d(0.36, 0.01);
                    gl.glVertex2d(0.36, -0.16);
                gl.glEnd();
            gl.glPopMatrix();
        }
        gl.glColor3d(0, 1, 1);
        gl.glPushMatrix();
            gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex2d(-0.35, -0.15);
                gl.glVertex2d(-0.35, 0);
                gl.glVertex2d(0.35, 0);
                gl.glVertex2d(0.35, -0.15);
            gl.glEnd();
        gl.glPopMatrix();
        if(opNaoSair){
            gl.glColor3d(1, 0, 0);
            gl.glPushMatrix();
                gl.glBegin(GL2.GL_POLYGON);
                    gl.glVertex2d(-0.36, -0.36);
                    gl.glVertex2d(-0.36, -0.19);
                    gl.glVertex2d(0.36, -0.19);
                    gl.glVertex2d(0.36, -0.36);
                gl.glEnd();
            gl.glPopMatrix();
        }
        gl.glColor3d(1, 1, 0);
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
            textRenderer.draw("Deseja Sair?", 520, 600);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.BLUE);
            textRenderer.draw("Sim", 520, 430);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.BLUE);
            textRenderer.draw("Não", 520, 335);
        textRenderer.endRendering();
    }
    
    public void desenhaMorteMenu(GL2 gl){
        gl.glColor3d(1, 1, 0);
        gl.glPushMatrix();
            gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex2d(-0.4, -0.6);
                gl.glVertex2d(-0.4, 0.5);
                gl.glVertex2d(0.4, 0.5);
                gl.glVertex2d(0.4, -0.6);
            gl.glEnd();
        gl.glPopMatrix();
        if(opTentarMorte){
            gl.glColor3d(1, 0, 0);
            gl.glPushMatrix();
                gl.glBegin(GL2.GL_POLYGON);
                    gl.glVertex2d(-0.36, -0.16);
                    gl.glVertex2d(-0.36, 0.01);
                    gl.glVertex2d(0.36, 0.01);
                    gl.glVertex2d(0.36, -0.16);
                gl.glEnd();
            gl.glPopMatrix();
        }
        gl.glColor3d(0, 1, 1);
        gl.glPushMatrix();
            gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex2d(-0.35, -0.15);
                gl.glVertex2d(-0.35, 0);
                gl.glVertex2d(0.35, 0);
                gl.glVertex2d(0.35, -0.15);
            gl.glEnd();
        gl.glPopMatrix();
        if(opMenuMorte){
            gl.glColor3d(1, 0, 0);
            gl.glPushMatrix();
                gl.glBegin(GL2.GL_POLYGON);
                    gl.glVertex2d(-0.36, -0.36);
                    gl.glVertex2d(-0.36, -0.19);
                    gl.glVertex2d(0.36, -0.19);
                    gl.glVertex2d(0.36, -0.36);
                gl.glEnd();
            gl.glPopMatrix();
        }
        gl.glColor3d(0, 1, 1);
        gl.glPushMatrix();
            gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex2d(-0.35, -0.35);
                gl.glVertex2d(-0.35, -0.20);
                gl.glVertex2d(0.35, -0.20);
                gl.glVertex2d(0.35, -0.35);
            gl.glEnd();
        gl.glPopMatrix();
        if(opSairMorte){
            gl.glColor3d(1, 0, 0);
            gl.glPushMatrix();
                gl.glBegin(GL2.GL_POLYGON);
                    gl.glVertex2d(-0.36, -0.56);
                    gl.glVertex2d(-0.36, -0.39);
                    gl.glVertex2d(0.36, -0.39);
                    gl.glVertex2d(0.36, -0.56);
                gl.glEnd();
            gl.glPopMatrix();
        }
        gl.glColor3d(0, 1, 1);
        gl.glPushMatrix();
            gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex2d(-0.35, -0.55);
                gl.glVertex2d(-0.35, -0.40);
                gl.glVertex2d(0.35, -0.40);
                gl.glVertex2d(0.35, -0.55);
            gl.glEnd();
        gl.glPopMatrix();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.BLUE);
            textRenderer.draw("Você Morreu!", 520, 650);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.BLUE);
            textRenderer.draw("Sua pontuação foi: " + pontuacao, 520, 600);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.BLUE);
            textRenderer.draw("Tentar de novo", 520, 430);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.BLUE);
            textRenderer.draw("Menu", 520, 335);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.BLUE);
            textRenderer.draw("Sair do Jogo", 520, 240);
        textRenderer.endRendering();
    }
    
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
    
    public void chamarSairMenu(){
        sairMenu = true;
        opSimSair = false;
        opNaoSair = true;
    }
    
    public void fecharSairMenu(){
        sairMenu = false;
    }
    
    public void chamarPausadoMenu(){
        pausadoMenu = true;
        opVoltarPause = true;
        opMenuPause = false;
        opSairPause = false;
    }
    
    public void fecharPausadoMenu(){
        pausadoMenu = false;
    }
    
    public void chamarMorteMenu(){
        morteMenu = true;
        opTentarMorte = true;
        opMenuMorte = false;
        opSairMorte = false;
        System.out.println(opTentarMorte);
    }
    
    public void fecharMorteMenu(){
        morteMenu = false;
    }
    
    public void iniciarJogo(){
        ovni = new OvniGato();
        pontuacao = 0;
        listaAsteroide = new ArrayList<Asteroide>();
        pausadoMenu = sairMenu = morteMenu = false;
        opVoltarPause = opMenuPause = opSairPause = false;
        opSimSair = opNaoSair = false;
        opTentarMorte = opMenuMorte = opSairMorte = false;
    }
    
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
}
