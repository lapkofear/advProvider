package com.company.domain;


import java.util.UUID;

public class Advertisement {

	private UUID id = UUID.randomUUID();
	private long createdOn = System.currentTimeMillis();
	private String name;
	private String source;
	private String category;

	public UUID getId() {
		return id;
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Advertisement that = (Advertisement) o;

		if (createdOn != that.createdOn)
			return false;
		if (id != null ? !id.equals(that.id) : that.id != null)
			return false;
		if (name != null ? !name.equals(that.name) : that.name != null)
			return false;
		if (source != null ? !source.equals(that.source) : that.source != null)
			return false;
		return category != null ? category.equals(that.category) : that.category == null;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (int) (createdOn ^ (createdOn >>> 32));
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (source != null ? source.hashCode() : 0);
		result = 31 * result + (category != null ? category.hashCode() : 0);
		return result;
	}
}
