package com.freecraft.freecraftbackend.service;

import com.freecraft.freecraftbackend.entity.ProductType;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class ProductService {

    private HashMap<String, String> productNames = new HashMap<String, String>();
    private HashMap<String, String> productDesc = new HashMap<String, String>();
    private HashMap<String, Integer> productAmounts = new HashMap<String, Integer>();
    private HashMap<String, Integer> productMemberAmounts = new HashMap<String, Integer>();
    private HashMap<String, ProductType> productTypes = new HashMap<String, ProductType>();

    private String CLAIM_DESC = "{0} blocs de protection";
    private String HEAD_DESC = "{0} têtes custom";
    private String MEMBERSHIP_DESC = "";


    private String CLAIM_METIER_NAME = "Pack Metier";
    private String CLAIM_ARCHITECTE_NAME = "Pack Architecte";
    private String CLAIM_ENTREPRISE_NAME = "Pack Entreprise";

    private String HEAD_ARTISTE_NAME = "Pack Artiste";
    private String HEAD_CREATEUR_NAME = "Pack Créateur";
    private String HEAD_MAITRE_NAME = "Pack Maître";

    private String PORTAL_DESC = "{0} portail de tp";
    private String PORTAL_NAME = "Portail de TP";
    private int PORTAL_NUMBER = 1;

    private int CLAIM_METIER_NUMBER = 1500;
    private int CLAIM_ARCHITECTE_NUMBER = 7500;
    private int CLAIM_ENTREPRISE_NUMBER = 24000;



    private int HEAD_ARTISTE_NUMBER = 5;
    private int HEAD_CREATEUR_NUMBER = 15;
    private int HEAD_MAITRE_NUMBER = 32;


    private int CLAIM_METIER_BONUS = 300;
    private int CLAIM_ARCHITECTE_BONUS = 1500;
    private int CLAIM_ENTREPRISE_BONUS = 4800;

    private int HEAD_ARTISTE_BONUS = 1;
    private int HEAD_CREATEUR_BONUS = 3;
    private int HEAD_MAITRE_BONUS = 5;

    private String MEMBERSHIP_NAME = "Statut membre";

    public ProductService() {

        productNames.put("prod_96ead62fd9c4e4", MEMBERSHIP_NAME);
        productDesc.put("prod_96ead62fd9c4e4", MEMBERSHIP_DESC);
        productAmounts.put("prod_96ead62fd9c4e4", 0);
        productMemberAmounts.put("prod_96ead62fd9c4e4", 0);
        productTypes.put("prod_96ead62fd9c4e4", ProductType.MEMBER);

        productTypes.put("prod_ab95ab18c49765", ProductType.MEMBER);
        productTypes.put("prod_0636fa7ba46940", ProductType.MEMBER);
        productTypes.put("prod_f1da1bb94e906c", ProductType.MEMBER);

        productNames.put("prod_8a6171f2c0d015", CLAIM_METIER_NAME);
        productDesc.put("prod_8a6171f2c0d015", CLAIM_DESC);
        productAmounts.put("prod_8a6171f2c0d015", CLAIM_METIER_NUMBER);
        productMemberAmounts.put("prod_8a6171f2c0d015", CLAIM_METIER_BONUS);
        productTypes.put("prod_8a6171f2c0d015", ProductType.CLAIM);

        productNames.put("prod_b1f2c358b9f143", CLAIM_ARCHITECTE_NAME);
        productDesc.put("prod_b1f2c358b9f143", CLAIM_DESC);
        productAmounts.put("prod_b1f2c358b9f143", CLAIM_ARCHITECTE_NUMBER);
        productMemberAmounts.put("prod_b1f2c358b9f143", CLAIM_ARCHITECTE_BONUS);
        productTypes.put("prod_b1f2c358b9f143", ProductType.CLAIM);

        productNames.put("prod_45a83f8f44a548", CLAIM_ENTREPRISE_NAME);
        productDesc.put("prod_45a83f8f44a548", CLAIM_DESC);
        productAmounts.put("prod_45a83f8f44a548", CLAIM_ENTREPRISE_NUMBER);
        productMemberAmounts.put("prod_45a83f8f44a548", CLAIM_ENTREPRISE_BONUS);
        productTypes.put("prod_45a83f8f44a548", ProductType.CLAIM);

        productNames.put("prod_510541df9aa640", HEAD_ARTISTE_NAME);
        productDesc.put("prod_510541df9aa640", HEAD_DESC);
        productAmounts.put("prod_510541df9aa640", HEAD_ARTISTE_NUMBER);
        productMemberAmounts.put("prod_510541df9aa640", HEAD_ARTISTE_BONUS);
        productTypes.put("prod_510541df9aa640", ProductType.HEAD);

        productNames.put("prod_6de3b8c57f33f6", HEAD_CREATEUR_NAME);
        productDesc.put("prod_6de3b8c57f33f6", HEAD_DESC);
        productAmounts.put("prod_6de3b8c57f33f6", HEAD_CREATEUR_NUMBER);
        productMemberAmounts.put("prod_6de3b8c57f33f6", HEAD_CREATEUR_BONUS);
        productTypes.put("prod_6de3b8c57f33f6", ProductType.HEAD);

        productNames.put("prod_948b28897d5035", HEAD_MAITRE_NAME);
        productDesc.put("prod_948b28897d5035", HEAD_DESC);
        productAmounts.put("prod_948b28897d5035", HEAD_MAITRE_NUMBER);
        productMemberAmounts.put("prod_948b28897d5035", HEAD_MAITRE_BONUS);
        productTypes.put("prod_948b28897d5035", ProductType.HEAD);

        productNames.put("prod_064677ba1e1032", PORTAL_NAME);
        productDesc.put("prod_064677ba1e1032", PORTAL_DESC);
        productAmounts.put("prod_064677ba1e1032", PORTAL_NUMBER);
        productMemberAmounts.put("prod_064677ba1e1032", 0);
        productTypes.put("prod_064677ba1e1032", ProductType.PORTAL);

    }

    public String getProductDesc(String product, String pseudo, String uuid, boolean member){
        String desc = productDesc.get(product);

        Integer amount = member ? productAmounts.get(product) + productMemberAmounts.get(product) : productAmounts.get(product);

        desc = desc.replace("{0}", amount.toString());

        desc = desc + " pour " + pseudo;// +  " \n(" + uuid + ")";

        return desc;

    }

    public ProductType getProductType(String product){

        return productTypes.get(product);

    }

    public int getProductAmount(String product, boolean member){

        return member ? productAmounts.get(product) + productMemberAmounts.get(product) : productAmounts.get(product);
    }


    public String getProductName(String product){

        return productNames.get(product);

    }




}
