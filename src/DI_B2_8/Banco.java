package DI_B2_8;

/**
 * Lógica del banco: clientes, sesión y operaciones.
 * Esta clase está TERMINADA. No la modifiques.
 */
public class Banco {

    public enum ResultadoLogin { CORRECTO, INCORRECTO, BLOQUEADA }

    private final Cliente[] clientes = {
        new Cliente("1234567812345678", "1234", "Homer Simpson", 0.37),
        new Cliente("8765432187654321", "4321", "Marge Simpson", 1250.00),
        new Cliente("1111222233334444", "0000", "Montgomery Burns", 999999.99)
    };

    private Cliente actual;
    private Cliente ultimoIntento;

    public ResultadoLogin login(String tarjeta, String pin) {
        ultimoIntento = null;
        for (Cliente c : clientes) {
            if (c.getTarjeta().equals(tarjeta)) {
                ultimoIntento = c;
                if (c.isBloqueada()) {
                    return ResultadoLogin.BLOQUEADA;
                }
                if (c.getPin().equals(pin)) {
                    c.reiniciarFallos();
                    actual = c;
                    return ResultadoLogin.CORRECTO;
                }
                c.registrarFallo();
                return c.isBloqueada() ? ResultadoLogin.BLOQUEADA : ResultadoLogin.INCORRECTO;
            }
        }
        return ResultadoLogin.INCORRECTO;
    }

    /** Intentos que le quedan a la última tarjeta probada, o -1 si la tarjeta no existe. */
    public int getIntentosRestantes() {
        return ultimoIntento == null ? -1 : ultimoIntento.getIntentosRestantes();
    }

    public void logout() { actual = null; }
    public boolean haySesion() { return actual != null; }
    public Cliente getClienteActual() { return actual; }
    public double getSaldoActual() { return actual.getSaldo(); }

    /** Indica si el PIN coincide con el del cliente que tiene la sesión abierta. */
    public boolean pinCorrecto(String pin) {
        return actual != null && actual.getPin().equals(pin);
    }

    /** Cambia el PIN del cliente que tiene la sesión abierta. */
    public void cambiarPin(String nuevo) {
        actual.setPin(nuevo);
    }

    public void ingresar(double cantidad) {
        actual.setSaldo(actual.getSaldo() + cantidad);
    }

    public boolean retirar(double cantidad) {
        if (cantidad > actual.getSaldo()) {
            return false;
        }
        actual.setSaldo(actual.getSaldo() - cantidad);
        return true;
    }
}
