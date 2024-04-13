package com.challenge.conversor.App;

import com.challenge.conversor.models.Conversor;
import com.challenge.conversor.models.ConversorRecord;
import com.challenge.conversor.models.Country;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Scanner;

public abstract class App {
    private static final List<Country> countries = List.of(
            new Country("ARS","Peso Argentino","Argentina"),
            new Country("BRL","Peso Brasilero","Brasil"),
            new Country("EUR","Euro","Unión Europea"),
            new Country("HKD","Dolar Hong Kong","Hong Kong"),
            new Country("JPY","Yen Japones","Japón"),
            new Country("KRW","Won Sur Coreano","Corea del sur"),
            new Country("RUB","Rublo Ruso","Rusia"),
            new Country("USD","Dolar Estados Unidos","Estados Unidos")
    );
    private static final Scanner input = new Scanner(System.in);

    public static void runApp(){
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new GsonBuilder()
                //.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .create();
        String url = "https://v6.exchangerate-api.com/v6/fbfc15dece75104b4f7cfbb6/pair/";
        int opc = -1,base,aConvertir;

        System.out.println("Sea bienvenido/a al Conversor de Moneda =]");
        while(opc != 0){
            opc = menu("Moneda Base:");
            if(opc == 0) break;
            else base = opc;

            opc = menu("Moneda a convertir:");
            if(opc == 0) break;
            else aConvertir = opc;

            var urlUpdate = url+"%s/%s".formatted(countries.get(base-1).currency_code(),countries.get(aConvertir-1).currency_code());
            //System.out.println(urlUpdate);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlUpdate))
                    .build();
            try{
                HttpResponse<String> response = client
                        .send(request,HttpResponse.BodyHandlers.ofString());
                //System.out.println(response.body());

                ConversorRecord cr = gson.fromJson(response.body(), ConversorRecord.class);
                //System.out.println(cr);

                Conversor conversor = new Conversor(cr);
                //System.out.println(conversor);

                System.out.println("Ingrese el valor que deseas convertir:");
                var value = validarDouble();

                var calculo = conversor.Conversion(value);
                System.out.printf("El valor %.2f [%s] corresponde al valor final de => %.2f [%s]%n", value,conversor.getBase_code(),calculo,conversor.getTarget_code());
                System.out.println("\nPresione cualquier tecla para continuar...");
                System.in.read();

            }catch (Exception e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("Finalizando...");
    }
    public static int menu(String subTitulo){
        int opc;
        System.out.println("*****************************************************");
        System.out.println(subTitulo);
        for(int x=0;x < countries.size();x++){
            System.out.printf("%d) %s (%s) %n", x+1,countries.get(x).currency_code(),countries.get(x).country_name());
        }
        System.out.println(
                """
                0) Salir\s
                *****************************************************
                Elija una opcion:
                """
        );
        opc = validarInput();
        return opc;
    }
    public static int validarInput(){
        int num;
        while(true){
            if(input.hasNextInt()){
                num = input.nextInt();
                if(num>-1 && num<=countries.size())
                    return num;
                else
                    System.out.println("Incorrecto. Valor fuera de las opciones. Ingrese el numero correcto: ");
            }
            else{
                System.out.println("Incorrecto.Ingrese un numero valido:");
                input.next();
            }
        }
    }
    public static double validarDouble(){
        double num;
        while(true){
            if(input.hasNextDouble()){
                num = input.nextDouble();
                if(num>=0){
                    return num;
                }
            }else{
                System.out.println("Incorrecto.Ingrese un numero valido:");
                input.next();
            }
        }
    }
}
