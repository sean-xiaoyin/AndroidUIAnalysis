package edu.usta.androidmt.model;

import java.util.Set;

public class InterPhrase implements Comparable<InterPhrase> {
    private String phrase;
    private double interest;
    private Set<PhrasePair> primePPs;
    public InterPhrase(String phrase, double interest, Set<PhrasePair> primePPs){
	this.phrase = phrase;
	this.interest = interest;
	this.primePPs = primePPs;
    }
    public String toString(){
	return this.phrase + " " + this.interest + " " + this.primePPs.toString();
    }
    public String getPhrase() {
	return phrase;
    }
    public void setPhrase(String phrase) {
	this.phrase = phrase;
    }
    public double getInterest() {
	return interest;
    }
    public void setInterest(double interest) {
	this.interest = interest;
    }
    @Override
    public int compareTo(InterPhrase iphrase) {
	return Double.compare(iphrase.interest, this.interest);
    }
    public Set<PhrasePair> getPrimePPs() {
	return primePPs;
    }
    public void setPrimePPs(Set<PhrasePair> primePPs) {
	this.primePPs = primePPs;
    }
}
