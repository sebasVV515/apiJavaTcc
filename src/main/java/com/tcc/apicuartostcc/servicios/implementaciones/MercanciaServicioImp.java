package com.tcc.apicuartostcc.servicios.implementaciones;

import com.tcc.apicuartostcc.entidades.Mercancia;
import com.tcc.apicuartostcc.entidades.Zona;
import com.tcc.apicuartostcc.repositorios.Mercanciarepositorio;
import com.tcc.apicuartostcc.repositorios.Zonarepositorio;
import com.tcc.apicuartostcc.servicios.ServicioGenerico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MercanciaServicioImp implements ServicioGenerico<Mercancia> {
    @Autowired
    Mercanciarepositorio mercanciarepositorio;
    @Autowired
    Zonarepositorio zonarepositorio;

    @Override
    public List<Mercancia> buscarTodos() throws Exception {
        try{
            List<Mercancia> mercancias=mercanciarepositorio.findAll();
            return mercancias;
        }catch(Exception error){
            throw new Exception(error.getMessage());
        }
    }

    @Override
    public Mercancia buscarPorId(Integer id) throws Exception {
        try{
            Optional<Mercancia> mercancia=mercanciarepositorio.findById(id);
            return mercancia.get();
        }catch(Exception error){
            throw new Exception(error.getMessage());
        }
    }

    @Override
    public Mercancia registrar(Mercancia tabla) throws Exception {
        try{
            Optional<Zona> zonaMercancia=zonarepositorio.findById(tabla.getZona().getId());
            Double capacidadZona=zonaMercancia.get().getDisponible();
            Double volumenMercancia= tabla.getVolumen();
            Double capacidadRestante = capacidadZona-volumenMercancia;
            if(capacidadRestante<0){
                throw new Exception("No hay espacio disponible en esta zona");
            }else{
                zonaMercancia.get().setDisponible(capacidadRestante);
                zonarepositorio.save(zonaMercancia.get());
                tabla=mercanciarepositorio.save(tabla);
                return tabla;
            }
        }catch(Exception error){
            throw new Exception(error.getMessage());
        }
    }

    @Override
    public Mercancia actualizar(Integer id, Mercancia tabla) throws Exception {
        try{
            Optional<Mercancia> mercanciaBuscada=mercanciarepositorio.findById(id);
            Mercancia mercancia=mercanciaBuscada.get();
            mercancia=mercanciarepositorio.save(tabla);
            return mercancia;
        }catch(Exception error){
            throw new Exception(error.getMessage());
        }
    }

    @Override
    public Boolean borrar(Integer id) throws Exception {
        try{
            if(mercanciarepositorio.existsById(id)){
                Optional<Mercancia> mercanciaRetirar=mercanciarepositorio.findById(id);
                Optional<Zona> zonaAsociada=zonarepositorio.findById(mercanciaRetirar.get().getZona().getId());
                Double capacidadOcupada=mercanciaRetirar.get().getVolumen();
                Double capacidadDisponible=zonaAsociada.get().getDisponible();
                Double nuevaCapacidad=capacidadDisponible+capacidadOcupada;
                zonaAsociada.get().setDisponible(nuevaCapacidad);
                zonarepositorio.save(zonaAsociada.get());
                mercanciarepositorio.deleteById(id);
                return true;
            }else{
                throw new Exception();
            }
        }catch(Exception error){
            throw new Exception(error.getMessage());
        }
    }
}
