package com.itbulls.nadine.spring.springbootdemo.service;

import com.itbulls.nadine.spring.springbootdemo.model.Booking;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;


import java.io.ByteArrayOutputStream;


@Service
public class TicketGeneratorService {

    public byte[] generateTicket(Booking booking) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document doc = new Document();
            PdfWriter.getInstance(doc, out);
            doc.open();

            doc.addTitle("Booking Ticket");
            doc.add(new Paragraph("🎟️ Ticket Confirmation"));
            doc.add(new Paragraph("Booking ID: " + booking.getId()));
            doc.add(new Paragraph("Event: " + booking.getEvent().getTitle()));
            doc.add(new Paragraph("Seat: " + (booking.getSeat() != null ? booking.getSeat().getCode() : "N/A")));
            doc.add(new Paragraph("Price: " + booking.getPrice() + " $"));
            doc.add(new Paragraph("Status: " + booking.getStatus()));
            doc.add(new Paragraph("Confirmed: " + booking.getConfirmed()));
            doc.add(new Paragraph("Payment Method: " + booking.getPaymentMethod()));
            doc.add(new Paragraph("Date: " + booking.getCreatedAt()));

            doc.close();
            return out.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

