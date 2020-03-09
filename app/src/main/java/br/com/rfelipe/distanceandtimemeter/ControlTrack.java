package br.com.rfelipe.distanceandtimemeter;

import android.location.Location;

public class ControlTrack {
    private Location CurrentLocation;
    private float distance;
    private boolean Ativo;

    ControlTrack() {
        distance = 0;
        Ativo = false;
    }

    public ControlTrack UpdatePercurso(Location location) {
        if (CurrentLocation == null)
            CurrentLocation = location;

        distance += CurrentLocation.distanceTo(location);
        CurrentLocation = location;

        return this;
    }

    public boolean getAtivo() {
        return Ativo;
    }

    public float getDistancia() {
        return distance;
    }

    public void setAtivo(boolean ativo) {
        Ativo = ativo;

        if (Ativo) {
            distance = 0;
            CurrentLocation = null;
        }
    }

    public double getLatitude(){
        return CurrentLocation == null ? 0 : CurrentLocation.getLatitude();
    }

    public double getLongitude(){
        return CurrentLocation == null ? 0 : CurrentLocation.getLongitude();
    }
}
