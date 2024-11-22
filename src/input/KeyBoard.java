package input;
import cena.Cena;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
/**
 *
 * @author Kakugawa
 */
public class KeyBoard implements KeyListener, MouseListener{
    private Cena cena;
    
    public KeyBoard(Cena cena){
        this.cena = cena;
    }
    
    @Override
    public void keyPressed(KeyEvent e) {        
        //System.out.println("Key pressed: " + e.getKeyCode());
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
            cena.pausar();
        }
        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            cena.ovniPular();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) { }
    
    @Override
    public void mouseClicked(MouseEvent e) {   
        int botao = e.getButton();

        if(botao == MouseEvent.BUTTON1){
            System.out.println("Clique ESQ");
            cena.click((float)e.getX(), (float)e.getY());
            System.out.println("clicou");
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseWheelMoved(MouseEvent e) {}

}
