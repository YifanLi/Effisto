package fr.inria.oak.effisto.Loader.StorageDescriptor;

public class StorageDescriptorParser {
	public String[] sd;
	//StorageDescriptor
	//[StorageAccess, AccessPattern + "+" + DistributionPattern]
	
	public String[] spliteSD(){
	if (sd.length == 2 && sd[1].contains("+")) {
	    // Split it.
		String[] sad = new String[]{sd[0], sd[1].split("\\+")[0], sd[1].split("\\+")[1]};
		return sad;
	} else {
	    throw new IllegalArgumentException("StorageDescriptor " + sd + " has something wrong within it");
	}
	}
	
	public String getStorageAccess(){
		if(spliteSD().length == 3){
			return spliteSD()[0];
		}else{
			return null;
		}
	}
	
	public String getAccessPattern(){
		if(spliteSD().length == 3){
			return spliteSD()[1];
		}else{
			return null;
		}
	}
	
	public String getDistributionPattern(){
		if(spliteSD().length == 3){
			return spliteSD()[2];
		}else{
			return null;
		}
	}

}
