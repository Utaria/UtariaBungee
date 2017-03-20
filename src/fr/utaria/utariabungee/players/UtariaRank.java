package fr.utaria.utariabungee.players;

public class UtariaRank {

	private int    id;
	private int    level;
	private String name;
	private String color;

	private boolean byDefault;

	private String  expiryTime;


	public UtariaRank(int id, String name, String color, int level, String expiryTime) {
		this.id         = id;
		this.name       = name;
		this.color      = color;
		this.level      = level;
		this.expiryTime = expiryTime;
	}

	public int     getId    () { return this.id;        }
	public String  getName  () { return this.name;      }
	public String  getColor () { return this.color;     }
	public int     getLevel () { return this.level;     }
	public boolean isDefault() { return this.byDefault; }

	public String  getExpiryTime() { return this.expiryTime; }
	public String  getPrefix    () { return (this.byDefault) ? "§" + this.color : "§" + this.color + "[" + this.name + "] "; }


	public boolean isInfinite() {
		return this.byDefault || this.expiryTime == null || this.expiryTime.equals("none");
	}


	public void    setName(String name)         { this.name       =   name; }
	public void    setColor(String color)       { this.color      =  color; }
	public void    setLevel(int level)          { this.level      =  level; }
	public void    setExpiryTime(String exTime) { this.expiryTime = exTime; }
	public void    setDefault(boolean b)        { this.byDefault  =      b; }



	public boolean equals(UtariaRank rank) {
		return this.id == rank.id;
	}

	@Override
	public String toString() {
		return "{UtariaRank #" + this.hashCode() + " (id=" + this.id + " level=" + this.level + " name=" +
				this.name + " color=" + this.color + " expiryTime=" + this.expiryTime + " default=" + this.byDefault + ")}";
	}

}
