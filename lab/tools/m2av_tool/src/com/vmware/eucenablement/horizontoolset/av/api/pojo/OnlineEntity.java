package com.vmware.eucenablement.horizontoolset.av.api.pojo;
/*
 * gson parsed object
 */
public class OnlineEntity {
	public String agent_status;
	public String connection_time;
	public String connection_time_human;
	public String entity_name;
	public String enetity_type;
	public float duration;
	public String duration_word;
	public String details;
public OnlineEntity() {
}
	public OnlineEntity(String agent_status, String connection_time,
			String connection_time_human, String entity_name,
			String enetity_type, float duration, String duration_word,
			String details) {
		super();
		this.agent_status = agent_status;
		this.connection_time = connection_time;
		this.connection_time_human = connection_time_human;
		this.entity_name = entity_name;
		this.enetity_type = enetity_type;
		this.duration = duration;
		this.duration_word = duration_word;
		this.details = details;
	}
}
