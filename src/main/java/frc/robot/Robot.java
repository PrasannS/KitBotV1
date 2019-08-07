/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PWMTalonSRX;
import edu.wpi.first.wpilibj.PWMVictorSPX;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This is a demo program showing the use of the RobotDrive class. The
 * SampleRobot class is the base of a robot application that will automatically
 * call your Autonomous and OperatorControl methods at the right time as
 * controlled by the switches on the driver station or the field controls.
 *
 * 
 * <p>The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SampleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 * <p>WARNING: While it may look like a good choice to use for your code if
 * you're inexperienced, don't. Unless you know what you are doing, complex code
 * will be much more difficult under this system. Use TimedRobot or
 * Command-Based instead if you're new.
 */
public class Robot extends SampleRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";

  private final SpeedControllerGroup l = new SpeedControllerGroup(new WPI_TalonSRX(0), new WPI_TalonSRX(1));
  private final SpeedControllerGroup r = new SpeedControllerGroup(new WPI_TalonSRX(2), new WPI_TalonSRX(3));

  private final DifferentialDrive m_robotDrive = new DifferentialDrive(l, r);
  private final Joystick m_stick = new Joystick(0);
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  private Gyro gyro;
  AHRS ahrs;

  //Encoder enc;
//enc = new Encoder(0, 1, false, Encoder.EncodingType.k4X);

  //Encoder declaration
  private Encoder encR = new Encoder(0, 1,false, Encoder.EncodingType.k4X);
  private Encoder encL = new Encoder(2, 3,false, Encoder.EncodingType.k4X);


  


  public Robot() {
    System.out.println("Gets to contructor");
    m_robotDrive.setExpiration(0.1);
    
    try {

			/***********************************************************************

			 * navX-MXP:

			 * - Communication via RoboRIO MXP (SPI, I2C, TTL UART) and USB.            

			 * - See http://navx-mxp.kauailabs.com/guidance/selecting-an-interface.

			 * 

			 * navX-Micro:

			 * - Communication via I2C (RoboRIO MXP or Onboard) and USB.

			 * - See http://navx-micro.kauailabs.com/guidance/selecting-an-interface.

			 * 

			 * Multiple navX-model devices on a single robot are supported.

			 ************************************************************************/

            ahrs = new AHRS(SPI.Port.kMXP); 

        } catch (RuntimeException ex ) {

            DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);

        }
    
    static final double kP = 0.03;

    static final double kI = 0.00;

    static final double kD = 0.00;

    static final double kF = 0.00;

    

    static final double kToleranceDegrees = 2.0f;    

    

    static final double kTargetAngleDegrees = 90.0f;
  }

  @Override
  public void robotInit() {
    
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto modes", m_chooser);
    encL.setDistancePerPulse(0.073631);
    encL.setReverseDirection(true);
    encL.setSamplesToAverage(7);
    encL.start();
    encR.start();
    gyro = new AnalogGyro(1);     
    //getting distances for logger
    encR.setDistancePerPulse(0.073631);
    encR.setReverseDirection(true);
    encR.setSamplesToAverage(7);
    ahrs.zeroYaw();
  }

  /**
   * 
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the if-else structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   *
   * <p>If you wanted to run a similar autonomous mode with an TimedRobot
   * you would write:
   *
   * <blockquote><pre>{@code
   * Timer timer = new Timer();
   *
   * // This function is run once each time the robot enters autonomous mode
   * public void autonomousInit() {
   *     timer.reset();
   *     timer.start();
   * }
   *
   * // This function is called periodically during autonomous
   * public void autonomousPeriodic() {
   * // Drive for 2 seconds
   *     if (timer.get() < 2.0) {
   *         myRobot.drive(-0.5, 0.0); // drive forwards half speed
   *     } else if (timer.get() < 5.0) {
   *         myRobot.drive(-1.0, 0.0); // drive forwards full speed
   *     } else {
   *         myRobot.drive(0.0, 0.0); // stop robot
   *     }
   * }
   * }</pre></blockquote>
   */
  @Override
  public void autonomous() {
    String autoSelected = m_chooser.getSelected();
    // String autoSelected = SmartDashboard.getString("Auto Selector",
    // defaultAuto);
    System.out.println("Auto selected: " + autoSelected);

    // MotorSafety improves safety when motors are updated in loops
    // but is disabled here because motor updates are not looped in
    // this autonomous mode.
    m_robotDrive.setSafetyEnabled(false);

    switch (autoSelected) {
      case kCustomAuto:
        // Spin at half speed for two seconds
        m_robotDrive.arcadeDrive(0.0, 0.5);
        Timer.delay(2.0);

        // Stop robot
        m_robotDrive.arcadeDrive(0.0, 0.0);
        break;
      case kDefaultAuto:
      default:
        // Drive forwards for two seconds
        m_robotDrive.arcadeDrive(-0.5, 0.0);
        Timer.delay(2.0);

        // Stop robot
        m_robotDrive.arcadeDrive(0.0, 0.0);
        break;
    }
  }

  /**
   * Runs the motors with arcade steering.
   *
   * <p>If you wanted to run a similar teleoperated mode with an TimedRobot
   * you would write:
   *
   * <blockquote><pre>{@code
   * // This function is called periodically during operator control
   * public void teleopPeriodic() {
   *     myRobot.arcadeDrive(stick);
   * }
   * }</pre></blockquote>
   */
  @Override
  public void operatorControl() {
    m_robotDrive.setSafetyEnabled(true);
    gyro.reset();
    while (isOperatorControl() && isEnabled()) {
      // Drive arcade style
      m_robotDrive.arcadeDrive(-m_stick.getY(), m_stick.getX());
      String s = -m_stick.getY()+" "+m_stick.getX();
      // The motors will be updated every 5ms
      SmartDashboard.putNumber("distance L",encL.getRaw());
     SmartDashboard.putNumber("distance R",encR.getRaw());
  SmartDashboard.putNumber("angle",ahrs.getAngle());
    SmartDashboard.putNumber("angularvelo",ahrs.getRawGyroX());
      Timer.delay(0.005);
      
    }
  }

  /**
   * Runs during test mode.
   */
  @Override
  public void test() {
  }
}

