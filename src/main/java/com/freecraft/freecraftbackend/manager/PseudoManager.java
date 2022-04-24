package com.freecraft.freecraftbackend.manager;

import com.freecraft.freecraftbackend.controllers.FaqController;
import com.freecraft.freecraftbackend.dto.MojangPlayerDto;
import com.freecraft.freecraftbackend.dto.ResponseStatusDto;
import com.freecraft.freecraftbackend.entity.ClaimPlayerData;
import com.freecraft.freecraftbackend.entity.FchData;
import com.freecraft.freecraftbackend.entity.PermissionPlayerData;
import com.freecraft.freecraftbackend.repo.ClaimPlayerDataRepository;
import com.freecraft.freecraftbackend.repo.FchDataRepository;
import com.freecraft.freecraftbackend.repo.PermissionPlayerDataRepository;
import com.freecraft.freecraftbackend.service.RestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PseudoManager {

    @Autowired
    private RestService restService;

    @Autowired
    private ClaimPlayerDataRepository claimPlayerDataRepository;

    @Autowired
    private FchDataRepository fchDataRepository;

    @Autowired
    private PermissionPlayerDataRepository permissionPlayerDataRepository;


    Logger logger = LoggerFactory.getLogger(FaqController.class);

    public String getPseudo(String uuid){
        Optional<PermissionPlayerData> perm = permissionPlayerDataRepository.findById(uuid);
        if(!perm.isPresent()){
            return null;
        }
        return perm.get().getUsername();
    }

    public int isMembre(String uuid){
        int ret = -1;
        Optional<ClaimPlayerData> claim = claimPlayerDataRepository.findById(uuid);
        Optional<PermissionPlayerData> perm = permissionPlayerDataRepository.findById(uuid);
;
        if (claim.isPresent() && perm.isPresent()) {
            ret = !perm.get().getGroup().equals("default") ? 1 : 0;
        }

        return ret;
    }


    public ResponseStatusDto getStatus(String pseudo) {

        MojangPlayerDto pdto = restService.getUUID(pseudo);
        if(pdto == null){
            return null;
        }
        Optional<ClaimPlayerData> claim = claimPlayerDataRepository.findById(pdto.getId());
        if(!claim.isPresent()){
            return null;
        }
        Optional<PermissionPlayerData> perm = permissionPlayerDataRepository.findById(pdto.getId());
        if(!perm.isPresent()){
            return null;
        }
        ResponseStatusDto retLinks = new ResponseStatusDto();
        if (claim.isPresent() && perm.isPresent()) {
            retLinks.setLastLogin(claim.get().getLastLogin());
            retLinks.setMember(!perm.get().getGroup().equals("default"));
            retLinks.setUuid(pdto.getId());
        }

        Optional<FchData> fch = fchDataRepository.findById(pdto.getId());
        if(!fch.isPresent()){
            return null;
        }

        retLinks.setUnlocked(fch.get().isUnlocked());

        return retLinks;
    }

}
