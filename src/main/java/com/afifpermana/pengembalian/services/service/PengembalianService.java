/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.afifpermana.pengembalian.services.service;

import com.afifpermana.pengembalian.services.entity.Pengembalian;
import com.afifpermana.pengembalian.services.repository.PengembalianRepository;
import com.afifpermana.pengembalian.services.vo.ResponseTemplateVO;
import com.afifpermana.pengembalian.services.vo.ResponseTemplateVOPinjam;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Apip
 */
@Service
public class PengembalianService {
    @Autowired
    private SimpleDateFormat formatTanggal;
    
    @Autowired
    private PengembalianRepository pengembalianRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    public Pengembalian savePengembalian(Pengembalian pengembalian) throws ParseException{
         
        ResponseTemplateVOPinjam peminjaman = restTemplate.getForObject("http://localhost:8001/peminjaman/"
                +pengembalian.getPeminjamanId(), ResponseTemplateVOPinjam.class);
        String tglSekarang = formatTanggal.format(new Date());
        long terlambat = kurangTanggal(tglSekarang,peminjaman.getPeminjaman().getTglKembali());
        double denda = terlambat * 500;
        pengembalian.setTerlambat((int)terlambat);
        pengembalian.setTglDiKembalikan(tglSekarang);
        pengembalian.setDenda(denda);
        return pengembalianRepository.save(pengembalian);
    }
    
    public ResponseTemplateVO getPengembalian(Long pengembalianId){
        ResponseTemplateVO vo = new ResponseTemplateVO();
        Pengembalian pengembalian = pengembalianRepository.findByPengembalianId(pengembalianId);
        ResponseTemplateVOPinjam peminjaman = restTemplate.getForObject("http://localhost:8001/peminjaman/"
                +pengembalian.getPeminjamanId(), ResponseTemplateVOPinjam.class);
        vo.setPengembalian(pengembalian);
        vo.setPeminjaman(peminjaman.getPeminjaman());
        return vo;
    }

    private long kurangTanggal(String tglAwal, String tglAkhir) throws ParseException {
//         SimpleDateFormat formatTanggal = new SimpleDateFormat("dd/MM/yyyy");
       Date tgl1 = formatTanggal.parse(tglAwal);
       Date tgl2 = formatTanggal.parse(tglAkhir);
       long selisih = tgl1.getTime() - tgl2.getTime();
       long selisihhari = selisih / (24 * 60 * 60 * 1000);
       return selisihhari;
    }
}
