package com.vmware.horizontoolset.ra;

import java.util.ArrayList;
import java.util.List;

public class HraManager {
	
	private static ArrayList<HraInvitation> invitations = new ArrayList<>(); 
	
	static {
//		add(HraInvitation._createTest());
//		add(HraInvitation._createTest());
//		add(HraInvitation._createTest());
	}
	
	public synchronized static void add(HraInvitation inv) {
		
		clearLegacy();
		
		inv.init();
		invitations.add(0, inv);
	}

	public synchronized static List<HraInvitation> list() {
		return new ArrayList<>(invitations);
	}
	
	private static void clearLegacy() {
		for (int i = invitations.size() - 1; i >= 0; i--) {
			HraInvitation inv = invitations.get(i);
			if (inv.isLegacy())
				invitations.remove(i);
			else
				break;
		}
	}

	public synchronized static boolean launch(int n) throws Exception {
		
		for (HraInvitation i : invitations) {
		
			if (i.id == n) {
				if (i.started)
					return false;
				
				i.launch();
				return true;
			}
		}
		return false;
	}

	public synchronized static HraInvitation get(int n) {
		for (HraInvitation i : invitations) {
			
			if (i.id == n) {
				i.started = true;
				return i;
			}
		}
		return null;
	}

}
