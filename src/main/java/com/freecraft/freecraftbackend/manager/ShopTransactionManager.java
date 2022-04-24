package com.freecraft.freecraftbackend.manager;

import com.fc.freecraftbackend.entity.*;
import com.fc.freecraftbackend.repo.*;
import com.freecraft.freecraftbackend.service.LogService;
import com.freecraft.freecraftbackend.service.ProductService;
import com.freecraft.freecraftbackend.service.RestService;
import com.freecraft.freecraftbackend.entity.*;
import com.freecraft.freecraftbackend.repo.*;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Date;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class ShopTransactionManager {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private FchDataRepository fchDataRepository;

    @Autowired
    private PermissionPlayerDataRepository permissionPlayerDataRepository;

    @Autowired
    private PermissionDataRepository permissionDataRepository;

    @Autowired
    private ClaimPlayerDataRepository claimPlayerDataRepository;

    @Autowired
    private HeadDataRepository headDataRepository;

    @Autowired
    private PortalDataRepository portalDataRepository;

    @Autowired
    private RestService restService;

    @Autowired
    private ProductService productService;

    @Autowired
    private LogService logService;

    @Autowired
    private PseudoManager pseudoManager;

    int activeSubscriptions = 99;
    int MAX_PLAYERS = 95;

    public String getLinkForProduct(String uuid, String product){

        int value = pseudoManager.isMembre(uuid);
        String pseudo = pseudoManager.getPseudo(uuid);

        if(value == -1 || pseudo == null) {
            return null;
        }

//        if(!pseudo.toLowerCase().equals("b0gg4d") && !pseudo.toLowerCase().equals("totalbasket47562")){
//            return null;
//        }

        boolean isMember = value == 0 ? false : true;

        CheckoutSession session = new CheckoutSession();

        session.setSuccess_url("https://free-craft.fr/shop#success");
        session.setCancel_url("https://free-craft.fr/shop#error");

        HashMap<String, Map<String,String>> bigmap = new HashMap<String, Map<String,String>>();
        HashMap<String,String> map = new HashMap<String, String>();
        map.put("uuid", uuid);
        map.put("server", "1");
        bigmap.put("metadata",map);
        session.setCustom(bigmap);
        session.setMetadata(map);

        Item item = new Item();
        item.setName(productService.getProductName(product) + " pour " + pseudo + (isMember ? " (membre)" : " (non membre)") );
        item.setDescription(productService.getProductDesc(product, pseudo, uuid, isMember));
        item.setProduct(product);

        if(productService.getProductType(product).equals(ProductType.MEMBER)){
            double price = getMembershipPrice();
            if(price == -1){
                return null;
            }
            item.setProduct(getQuadernoMembershipSku(price));
        }

        Item[] items = { item };
        session.setItems(items);

        ResponseEntity<CheckoutSession> var = restService.createSession(session);

        if(var.getStatusCode() == HttpStatus.CREATED){

            return createTransaction(var.getBody() ,uuid);

        }

        return null;

    }

    public String cancelMembership(String uuid){

        int value = pseudoManager.isMembre(uuid);
        String pseudo = pseudoManager.getPseudo(uuid);

        if(value < 1 || pseudo == null) {
            return null;
        }

        Optional<FchData> data = fchDataRepository.findById(uuid);
        if(!data.isPresent()){
            return null;
        }

        if(!data.get().isUnlocked()){
            return null;
        }

        SubscriptionCollection subs = restService.getSubscriptions();

        List<Subscription> sublist = subs.getData().stream().filter(s ->
            !s.getCancelAtPeriodEnd()
        ).collect(Collectors.toList());

        if(subs.getData().size() == 0){
            logService.logWarning("0 subscription found, is it normal ?");
        }

        AtomicBoolean error = new AtomicBoolean(false);
        AtomicBoolean success = new AtomicBoolean(false);

        sublist.forEach(s ->{
            String id = s.getMetadata().get("uuid");
            if(id == null){
                logService.logError("can't find uuid for subscription "+ s.getId());
            }else if (id.equals(uuid)){
                String res = restService.cancelSubscription(s);
                if(res == null){
                    logService.logInfo("abonnement annulé "+ s.getId());
                    success.set(true);
                }else {
                    error.set(true);
                    logService.logError("can't cancel subscription "+ s.getId() + " " + res);
                }
            }
        });

        if(error.get()){

            return null;
        }

        if(success.get()){
            activeSubscriptions--;
            logService.logInfo("abonnement annulé pour "+ pseudo +" (" + uuid + ")");
            return "Abonnement annulé";
        }


        return "Aucun abonnement n'a été trouvé";

    }


    String createTransaction(CheckoutSession session, String uuid){

        Transaction transaction = new Transaction();
        java.util.Date utilDate = new java.util.Date();
        transaction.setCreationDate(new Date(utilDate.getTime()));
        transaction.setUuid(uuid);
        transaction.setId(session.getId());
        transaction.setProduct(session.getItems()[0].getProduct());
        transaction = transactionRepository.save(transaction);

        if(!transactionRepository.findById(transaction.getId()).isPresent()) {
            return null;
        }

        return session.getPermalink();
    }

    void checkMembers(boolean checkAll) throws Exception{
        List<String> membersUUID = new ArrayList<String>();
        SubscriptionCollection subs = restService.getSubscriptions();

        if(subs == null) {
            throw new Exception("retrieved subs are null");
        }
        List<Subscription> data = subs.getData();
        if(data == null) {
            throw new Exception("retrieved subs data is null");
        }
        activeSubscriptions = data.size();
        data.forEach(
                sub -> {
                    String uuid = sub.getMetadata().get("uuid");
                    if(uuid == null){
                        logService.logError("can't find uuid for subscription "+ sub.getId());
                    }else{
                        membersUUID.add(uuid);
                    }
                }
        );

        permissionPlayerDataRepository.findAll().forEach(p -> {

            if(membersUUID.contains(p.getUiid())){
                if(p.getGroup().equals("default")){
                    p.setGroup("membre");
                    permissionPlayerDataRepository.save(p);
                }
                    List<PermissionData> perms = permissionDataRepository.findByUuid(p.getUiid());
                    perms = perms.stream().filter(per -> per.getPermission().equals("group.membre")).collect(Collectors.toList());
                    if(perms.size() <= 0){
                        PermissionData newperm = new PermissionData();
                        newperm.setPermission("group.membre");
                        newperm.setUuid(p.getUiid());
                        permissionDataRepository.save(newperm);
                        fchDataRepository.setUnlocked(false,p.getUiid());
                    }

            }else if(checkAll || p.getGroup().equals("membre")) {
                if(p.getGroup().equals("membre")){
                    p.setGroup("default");
                    permissionPlayerDataRepository.save(p);
                }
                    List<PermissionData> perms = permissionDataRepository.findByUuid(p.getUiid());
                    perms = perms.stream().filter(per -> per.getPermission().equals("group.membre")).collect(Collectors.toList());
                    if(perms.size() > 0){
                        perms.forEach(per -> {
                            permissionDataRepository.delete(per);
                        });
                    }
            }
        });
        runScreenCMD("lp sync");
    }

    void executeTransaction(Transaction t){
        logService.logInfo("executed transaction "+t.getId()+" uuid="+t.getUuid()+" product="+t.getProduct());
        t.setExecuted(true);
        java.util.Date utilDate = new java.util.Date();
        t.setExecutionDate(new Date(utilDate.getTime()));
        if(transactionRepository.save(t) == null){
            logService.logInfo("Can't save executed transaction for " + t.getUuid() + " : "+ t.getId()+" product="+t.getProduct());
        }
    }


    public boolean checkSession(int sess) {
        Optional<Transaction> a = transactionRepository.findById(sess);
        return a.isPresent() && !a.get().getExecuted();
    }

    public double getMembershipPrice(){
        if(activeSubscriptions < MAX_PLAYERS * 0.5){
            return 1.99;
        }else if (activeSubscriptions < MAX_PLAYERS *0.75){
            return 3.99;
        }else if (activeSubscriptions < MAX_PLAYERS){
            return 7.99;
        }else{
            return -1;
        }
    }

    public String getQuadernoMembershipSku(double price){
        if(price == 0.5){
            return "prod_ab95ab18c49765";
        }else if(price == 3.99){
            return "prod_0636fa7ba46940";
        }else if(price == 7.99){
            return "prod_f1da1bb94e906c";
        }else{
            return "prod_96ead62fd9c4e4";
        }
    }


    public boolean performTransactions(){

        List<Transaction> transactions = transactionRepository.getRecentTransaction();

        AtomicBoolean executedMember = new AtomicBoolean(false);

        transactions.forEach(
                t -> {
                    try {
                        CheckoutSession sess = restService.getSession(t.getId().toString()).getBody();
                        String s = sess.getStatus();
                        if(s.equals("completed")){
                            //TODO execute
                            if(productService.getProductType(t.getProduct()) == ProductType.CLAIM){

                                boolean bMembre = pseudoManager.isMembre(t.getUuid()) == 1 ? true : false;
                                int addClaimBlocks = productService.getProductAmount(t.getProduct(), bMembre);
                                Optional<ClaimPlayerData> opt = claimPlayerDataRepository.findById(t.getUuid());
                                if(opt.isPresent()){
                                    ClaimPlayerData claims = opt.get();
                                    final int beforeBlocs = claims.getBonusBlocks();
                                    setBuying(t.getUuid(), true);
                                    sleep(50);
                                    if(kickPlayer(t.getUuid())){
                                        int iter = 0;
                                        sleep(50);
                                        while(isOnline(t.getUuid())){
                                            iter++;
                                            sleep(50);
                                            if(iter > 150){
                                                break;
                                            }
                                        }
                                        if(!isOnline(t.getUuid())){
                                            claims.setBonusBlocks(claims.getBonusBlocks() + addClaimBlocks);
                                            final int afterBlocs = claims.getBonusBlocks();
                                            if(claimPlayerDataRepository.save(claims) != null){
                                                if(!isOnline(t.getUuid())){
                                                    executeTransaction(t);
                                                }else{
                                                    logService.logError("Attention " + t.getUuid() + " : "+ t.getId() +". Player connected while performing transaction ! Transaction executed but not registered.");
                                                }
                                                logService.logInfo(beforeBlocs+" > "+afterBlocs);
                                            }else{
                                                logService.logError("Can't execute transaction for " + t.getUuid() + " : "+ t.getId() +". Can't save claim data");
                                            }
                                        }else{
                                            logService.logError("Can't execute transaction for " + t.getUuid() + " : "+ t.getId() +". Player online");
                                        }
                                    }else{
                                        logService.logError("Can't execute transaction for " + t.getUuid() + " : "+ t.getId() +". Can't run command");
                                    }
                                    setBuying(t.getUuid(), false);
                                }else{
                                    logService.logError("Can't execute transaction for " + t.getUuid() + " : "+ t.getId() +". Can't retrieve claim data");
                                }

                            }else if(productService.getProductType(t.getProduct()) == ProductType.HEAD){

                                boolean bMembre = pseudoManager.isMembre(t.getUuid()) == 1 ? true : false;
                                int addHeads = productService.getProductAmount(t.getProduct(), bMembre);
                                Optional<HeadData> opt = headDataRepository.findById(t.getUuid());
                                if(opt.isPresent()){
                                    HeadData heads = opt.get();
                                    final int beforeHeads = heads.getHeads();
                                    setBuying(t.getUuid(), true);
                                    sleep(50);
                                    if(kickPlayer(t.getUuid())){
                                        int iter = 0;
                                        sleep(50);
                                        while(isOnline(t.getUuid())){
                                            iter++;
                                            sleep(50);
                                            if(iter > 150){
                                                break;
                                            }
                                        }
                                        if(!isOnline(t.getUuid())){
                                            heads.setHeads(heads.getHeads() + addHeads);
                                            final int afterHeads = heads.getHeads();
                                            if(headDataRepository.save(heads) != null){
                                                if(!isOnline(t.getUuid())){
                                                    executeTransaction(t);
                                                }else{
                                                    logService.logError("Attention " + t.getUuid() + " : "+ t.getId() +". Player connected while performing transaction ! Transaction executed but not registered.");
                                                }
                                                logService.logInfo(beforeHeads+" > "+afterHeads);
                                            }else{
                                                logService.logError("Can't execute transaction for " + t.getUuid() + " : "+ t.getId() +". Can't save claim data");
                                            }
                                        }else{
                                            logService.logError("Can't execute transaction for " + t.getUuid() + " : "+ t.getId() +". Player online");
                                        }
                                    }else{
                                        logService.logError("Can't execute transaction for " + t.getUuid() + " : "+ t.getId() +". Can't run command");
                                    }
                                    setBuying(t.getUuid(), false);

                                }else{
                                    logService.logError("Can't execute transaction for " + t.getUuid() + " : "+ t.getId() +". Can't retrieve claim data");
                                }

                            }else if(productService.getProductType(t.getProduct()) == ProductType.PORTAL){

                            boolean bMembre = pseudoManager.isMembre(t.getUuid()) == 1 ? true : false;
                            int addPortals = productService.getProductAmount(t.getProduct(), bMembre);
                            Optional<PortalData> opt = portalDataRepository.findById(t.getUuid());
                            if(opt.isPresent()){
                                PortalData portals = opt.get();
                                final int beforePortals = portals.getPortals();
                                setBuying(t.getUuid(), true);
                                sleep(50);
                                if(kickPlayer(t.getUuid())){
                                    int iter = 0;
                                    sleep(50);
                                    while(isOnline(t.getUuid())){
                                        iter++;
                                        sleep(50);
                                        if(iter > 150){
                                            break;
                                        }
                                    }
                                    if(!isOnline(t.getUuid())){
                                        portals.setPortals(portals.getPortals() + addPortals);
                                        final int afterPortals = portals.getPortals();
                                        if(portalDataRepository.save(portals) != null){
                                            if(!isOnline(t.getUuid())){
                                                executeTransaction(t);
                                            }else{
                                                logService.logError("Attention " + t.getUuid() + " : "+ t.getId() +". Player connected while performing transaction ! Transaction executed but not registered.");
                                            }
                                            logService.logInfo(beforePortals+" >> "+afterPortals);
                                        }else{
                                            logService.logError("Can't execute transaction for " + t.getUuid() + " : "+ t.getId() +". Can't save claim data");
                                        }
                                    }else{
                                        logService.logError("Can't execute transaction for " + t.getUuid() + " : "+ t.getId() +". Player online");
                                    }
                                }else{
                                    logService.logError("Can't execute transaction for " + t.getUuid() + " : "+ t.getId() +". Can't run command");
                                }
                                setBuying(t.getUuid(), false);

                            }else{
                                logService.logError("Can't execute transaction for " + t.getUuid() + " : "+ t.getId() +". Can't retrieve claim data");
                            }

                        }else if(productService.getProductType(t.getProduct()) == ProductType.MEMBER){
                                if(!executedMember.get()){
                                    checkMembers(false);
                                    executedMember.set(true);
                                }
                                executeTransaction(t);
                            }else{
                                throw new Exception("unknown product");
                            }

                        }else if(s.equals("failed") || s.equals("abandoned")){
                            transactionRepository.delete(t);
                        }

                    }catch(Exception e){

                        logService.logError("can't check transaction "+t.getId()+" uuid="+t.getUuid()+" product="+t.getProduct());
                        logService.logError(e.toString());

                    }


                }
        );

        return true;

    }

    boolean runScreenCMD(String c){
        String[] cmd = new String[] {"sudo", "-u", "minecraft", "screen", "-S", "minecraft-screen", "-p", "0", "-X", "stuff", c+"^M"};
        Runtime run = Runtime.getRuntime();
        Process pr = null;
        try {
            pr = run.exec(cmd);
            pr.waitFor();
        } catch (IOException | InterruptedException e) {
            logService.logError(e.getMessage());
            return false;
        }

        return true;

    }

    boolean adjustClaimBlocks(String uuid, int blocs){
        String pseudo = pseudoManager.getPseudo(uuid);
        if(pseudo == null){
            return false;
        }
        String cmd = "adjustbonusclaimblocks "+ pseudo + " " + blocs;
        return runScreenCMD(cmd);
    }

    boolean isOnline(String uuid){
        Optional<FchData> player = fchDataRepository.findById(uuid);
        return player.isPresent() && player.get().isOnline();
    }

    boolean setBuying(String uuid, boolean b){

        int modified = fchDataRepository.setBuying(b, uuid);

        return modified > 0;

//        Optional<FchData> player = fchDataRepository.findById(uuid);
//        FchData data = null;
//        if(player.isPresent()){
//            player.get().setBuying(b);
//            data = fchDataRepository.save(player.get());
//        }
    }

    void sleep(int ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    boolean kickPlayer(String uuid){
        String pseudo = pseudoManager.getPseudo(uuid);
        if(pseudo == null){
            return false;
        }
        String cmd = "kick "+ pseudo +" &2Livraison des achats : veuillez attendre 30 secondes avant de vous reconnecter.";

        return runScreenCMD(cmd);

    }


}
