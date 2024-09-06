package org.luubstar.lsdatabase.Utils;

import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ExportExcel {
    public static boolean exportExcel(String nombre, String fecha, String IVA, String IRPF, List<List<String>> c, File s){
        try{
            Map<String, Object> data = new HashMap<>();
            data.put("NombrePlantilla", "Mi Empresa Creadora");

            try (InputStream is = Objects.requireNonNull(ExportExcel.class.getResourceAsStream("plantilla.xlsx"))) {
                try (FileOutputStream os = new FileOutputStream(s)) {
                    Context context = new Context();
                    context.putVar("NombrePlantilla", nombre);
                    context.putVar("FechaLimite", fecha);
                    context.putVar("IVA", IVA);
                    context.putVar("IRPF", IRPF);
                    context.putVar("clientes", c);
                    JxlsHelper.getInstance().processTemplate(is, os, context);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
