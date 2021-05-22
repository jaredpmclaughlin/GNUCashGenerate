package generateCash;

public class Date {

	private int month;
	private int day;
	private int year;
	
	// this is just for me, so make a lot of assumptions
	Date(String date){
		String[] tmp;
		tmp = date.split("/");
		this.month = Integer.valueOf(tmp[0]);
		this.day = Integer.valueOf(tmp[1]);
		this.year = Integer.valueOf(tmp[2]);
	}
	
	public int getMonth() {
		return this.month;
	}
	
	public int getDay() {
		return this.day;
	}
	
	public int getYear() {
		return this.year;
	}
	
	public void nextMonth() {
		this.month++;
		if( this.month == 13) {
			this.month = 1;
			this.year++;
		}
		
		// assume that dates at the end of the month are meant to be end of month
		switch(month) {
		case 3:
			if(this.day == 28) this.day = 31;
			break;
		case 5: // 30 -> 31
		case 7: // 30 -> 31
		case 10: //30 -> 31
		case 12: //30 -> 31
			if(this.day == 30) this.day = 31;
			break;
		case 2:
			if ( this.day == 31) this.day = 28; 
			break;
		case 4: // 31-> 30
		case 6: //31 -> 30
		case 9: // 31 -> 30
		case 11: // 31 -> 30
			if(this.day == 31) this.day = 30;
			break;
		}

	}
	
	public void nextDate() {
		switch(month) {
			case 1:
				// months with 31 days
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				if (this.day == 31) {
					this.month++;
					this.day = 0;
					if(this.month==13) {
						this.month=1;
						this.year++;
					}
				}
				break;
			case 2:
				if(this.day == 28) {
					this.month++;
					this.day = 0;
				}
			//to heck with leap years
				break;
			case 4:
				// 30 days
			case 6:
			case 9:
			case 11:
				if(this.day == 30) {
					this.month++;
					this.day = 0;
				}				
				break;
			}
		this.day++;
	}
	
	public void nextYear() {
		this.year++;
	}
	
	public String toString() {
		String dateString = this.month + "/" + this.day + "/" + this.year;
		return dateString;
	}
	
	public int monthBetween(Date to) {
		return 0;
	}
	
	public int daysBetween(Date to) {
		
		Date tmp;
		int days = 0;

		if(this.lessThan(to)) {
			tmp = new Date(this.toString());
			for(;tmp.getYear() < to.getYear(); days+= 365) tmp.nextYear(); 
			for(;tmp.getMonth() < to.getMonth(); days++) tmp.nextDate();
			for(;tmp.getDay() < to.getDay(); days++ ) tmp.nextDate();
		}
		
		if(to.lessThan(this)) {
			tmp = new Date(to.toString());
			for(;tmp.getYear() < this.getYear(); days+= 365) tmp.nextYear(); 
			for(;tmp.getMonth() < this.getMonth(); days++) tmp.nextDate();
			for(;tmp.getDay() < this.getDay(); days++ ) tmp.nextDate();
		}
		
		return days;
	}
	
	// specifically avoiding extending Comparable
	public boolean equals(Date to) {
		if(this.year != to.getYear()) return false;
		if(this.month != to.getMonth()) return false;
		if(this.day != to.getDay()) return false;
		return true;
	}
	
	public boolean lessThan(Date to) {
		if(this.year > to.getYear()) return false;
		if(this.year < to.getYear()) return true;
		
		if(this.month == to.getMonth()) {
			if(this.day <= to.getDay()) return true;
			else return false;
		}
		return true;
	}

}
