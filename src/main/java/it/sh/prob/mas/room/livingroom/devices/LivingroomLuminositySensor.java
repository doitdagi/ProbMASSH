package it.sh.prob.mas.room.livingroom.devices;

import java.util.Random;

import it.sh.prob.mas.SHDeviceAgent;
import it.sh.prob.mas.SHParameters;
import it.sh.prob.mas.room.livingroom.utilities.LivingroomLumValues;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class LivingroomLuminositySensor extends SHDeviceAgent {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private static final LivingroomLumValues[] lumValues = LivingroomLumValues.values();
		private static final String PROBLOG_VARIABLE = "luminicity";
		@Override
		protected void setup() {
			addBehaviour(new RegisterRelevantSHServices());
			addBehaviour(new HandleLuminosityRequest());
//			addBehaviour(new InformCurrentLumionisity(this, 1000));
		}

		private class RegisterRelevantSHServices extends OneShotBehaviour {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				registerRelevantSHServices(SHParameters.LIVINGROOM_LIGHT_SENSOR);
			}
		}

		private class HandleLuminosityRequest extends CyclicBehaviour {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
				ACLMessage msg = blockingReceive(mt);
				if (msg != null) {
					ACLMessage reply = msg.createReply();
					reply.setPerformative(ACLMessage.INFORM);
					reply.setContent(formulateReply(PROBLOG_VARIABLE));
					myAgent.send(reply);
				} else {
					block();
				}
			}

		}

		/**
		 * 
		 * @author fd
		 *
		 */
		private class InformCurrentLumionisity extends TickerBehaviour {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public InformCurrentLumionisity(Agent a, long period) {
				super(a, period);
			}

			@Override
			protected void onTick() {
				AID reasonerAgent = new AID("reasoner", AID.ISLOCALNAME);
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.addReceiver(reasonerAgent);
				msg.setContent(generateRandomDeviceValues()); // TODO: HERE RANDOMLY SELECT THE ACTIVITY
				myAgent.send(msg);
			}
		}

		@Override
		protected String generateRandomDeviceValues() {
			int rnd = new Random().nextInt(lumValues.length);
			return lumValues[rnd].toString();
		}

	}
