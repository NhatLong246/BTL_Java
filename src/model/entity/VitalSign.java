package model.entity;

import java.time.LocalDateTime;

public class VitalSign {
    private String vitalSignID;
    private String patientID;
    private double temperature; // Nhiệt độ
    private int systolicPressure; // Huyết áp tâm thu
    private int diastolicPressure; // Huyết áp tâm trương
    private int heartRate; // Nhịp tim
    private double oxygenSaturation; // Độ bão hòa oxy
    private LocalDateTime recordedAt; // Thời điểm đo

    public VitalSign() {
        this.recordedAt = LocalDateTime.now(); // Lấy thời gian thực tế
    }

    public VitalSign(String vitalSignID, String patientID, double temperature, int systolicPressure, 
                     int diastolicPressure, int heartRate, double oxygenSaturation, LocalDateTime recordedAt) {
        this.vitalSignID = vitalSignID;
        this.patientID = patientID;
        setTemperature(temperature);
        setSystolicPressure(systolicPressure);
        setDiastolicPressure(diastolicPressure);
        setHeartRate(heartRate);
        setOxygenSaturation(oxygenSaturation);
        this.recordedAt = recordedAt;
    }

    public String getVitalSignID() {
        return vitalSignID;
    }

    public void setVitalSignID(String vitalSignID) {
        this.vitalSignID = vitalSignID;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        if (temperature < 30 || temperature > 45) {
            throw new IllegalArgumentException("Nhiệt độ phải trong khoảng 30 - 45°C.");
        }
        this.temperature = temperature;
    }

    public int getSystolicPressure() {
        return systolicPressure;
    }

    public void setSystolicPressure(int systolicPressure) {
        if (systolicPressure < 50 || systolicPressure > 250) {
            throw new IllegalArgumentException("Huyết áp tâm thu phải trong khoảng 50 - 250 mmHg.");
        }
        this.systolicPressure = systolicPressure;
    }

    public int getDiastolicPressure() {
        return diastolicPressure;
    }

    public void setDiastolicPressure(int diastolicPressure) {
        if (diastolicPressure < 30 || diastolicPressure > 150) {
            throw new IllegalArgumentException("Huyết áp tâm trương phải trong khoảng 30 - 150 mmHg.");
        }
        this.diastolicPressure = diastolicPressure;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        if (heartRate < 30 || heartRate > 250) {
            throw new IllegalArgumentException("Nhịp tim phải trong khoảng 30 - 250 bpm.");
        }
        this.heartRate = heartRate;
    }

    public double getOxygenSaturation() {
        return oxygenSaturation;
    }

    public void setOxygenSaturation(double oxygenSaturation) {
        if (oxygenSaturation < 50 || oxygenSaturation > 100) {
            throw new IllegalArgumentException("SpO2 phải trong khoảng 50 - 100%.");
        }
        this.oxygenSaturation = oxygenSaturation;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }
}