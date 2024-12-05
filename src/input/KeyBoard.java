package input;
import cena.Cena;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
/**
 *
 * @author Kakugawa
 */
public class KeyBoard implements KeyListener{
    private Cena cena;
    
    public KeyBoard(Cena cena){
        this.cena = cena;
    }
    
    @Override
    public void keyPressed(KeyEvent e) {        
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE){ // Chama método que determina o que cada tecla vai fazer
            cena.teclaEsc();
        }
        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            cena.teclaSpaco();
        }
        if(e.getKeyCode() == KeyEvent.VK_UP){
            cena.teclaCima();
        }
        if(e.getKeyCode() == KeyEvent.VK_DOWN){
            cena.teclaBaixo();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) { }
    
    
}
