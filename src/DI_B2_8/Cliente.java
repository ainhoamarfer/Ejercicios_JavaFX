package DI_B2_8;
public class Cliente {

    private final String tarjeta;
    private String pin;
    private final String nombre;
    private double saldo;
    private int fallos = 0;

    public Cliente(String tarjeta, String pin, String nombre, double saldo) {
        this.tarjeta = tarjeta;
        this.pin = pin;
        this.nombre = nombre;
        this.saldo = saldo;
    }

    public String getTarjeta() { return tarjeta; }
    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }
    public String getNombre() { return nombre; }
    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }

    public boolean isBloqueada() { return fallos >= 3; }
    public int getIntentosRestantes() { return 3 - fallos; }
    public void registrarFallo() { fallos++; }
    public void reiniciarFallos() { fallos = 0; }
}
