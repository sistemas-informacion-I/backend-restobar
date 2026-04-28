package org.restobar.gaira.modulo_acceso.service.perfil;

import org.restobar.gaira.modulo_acceso.dto.perfil.CambioPasswordRequest;
import org.restobar.gaira.modulo_acceso.dto.perfil.PerfilPersonalResponse;
import org.restobar.gaira.modulo_acceso.dto.perfil.PerfilPersonalUpdate;

public interface PerfilPersonalService {
    PerfilPersonalResponse obtenerMiPerfil();
    PerfilPersonalResponse actualizarMiPerfil(PerfilPersonalUpdate update);
    void cambiarMiPassword(CambioPasswordRequest request);
    void eliminarMiPerfil();
}
