package org.example;

import entities.Product;
import service.ProductService;

public class Main {



    public static void main(String[] args) {

        Product p1=new Product(14,"555k","khadija", (float) 2F,54,"small","shirt");
        Product p2=new Product("1234l","mmm", (float) 15,85,"L","t-shirt");
        Product p3=new Product("458k","ppp", (float) 14,85,"S","t-shirt");
        Product p4=new Product("4751l","iiii", (float) 18,85,"xS","t-shirt");
        Product p5=new Product("956jk","sss", (float) 19,85,"xxL","t-shirt");
        Product p6=new Product("3257k","hhhhh", (float) 20,85,"41","sneakers");

        ProductService ps=new ProductService();
        ps.add(p2);
        ps.add(p3);
        ps.add(p4);
        ps.add(p5);
        ps.add(p6);
       //ps.delete(p1);

       //ps.Update(p1);

        ps.getAll().forEach(System.out::println);
    }
}