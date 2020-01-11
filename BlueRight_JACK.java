package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


@Autonomous
public class BlueRight_JACK extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();

    static final double     COUNTS_PER_MOTOR_REV    = 1680;
    static final double     DRIVE_GEAR_REDUCTION    = 1.0;
    static final double     WHEEL_DIAMETER_INCHES   = 4.0;
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double     DRIVE_SPEED             = 0.9;
    static final double     TURN_SPEED              = 0.5;

    private DcMotor left = null;
    private DcMotor right = null;
    private DcMotor front = null;
    private DcMotor back = null;
    private DcMotor claw = null;
    private DcMotor lift = null;
    private ColorSensor frontCS = null;
    private DistanceSensor frontDS = null;
    private ColorSensor platCS = null;
    private DistanceSensor platDS = null;
    private Servo leftServo = null;
    private Servo rightServo = null;

    public float HSVF[] = {0f, 0f, 0f};
    public float HSVD[] = {0f, 0f, 0f};

    final double SCALE_FACTOR = 255;

    @Override
    public void runOpMode() {

        left = hardwareMap.get(DcMotor.class, "B");
        right = hardwareMap.get(DcMotor.class, "F");
        front = hardwareMap.get(DcMotor.class, "L");
        back = hardwareMap.get(DcMotor.class, "R");
        claw = hardwareMap.get(DcMotor.class, "C");
        lift = hardwareMap.get(DcMotor.class, "S");

        leftServo = hardwareMap.get(Servo.class, "LS");
        rightServo = hardwareMap.get(Servo.class, "RS");

        left.setDirection(DcMotor.Direction.REVERSE);
        right.setDirection(DcMotor.Direction.FORWARD);
        front.setDirection(DcMotor.Direction.REVERSE);
        back.setDirection(DcMotor.Direction.FORWARD);
        lift.setDirection(DcMotor.Direction.REVERSE);

        leftServo.setPosition(0.0);
        rightServo.setPosition(1.0);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Resetting Encoders");
        telemetry.update();

        left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        front.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        back.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        front.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        back.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        // Send telemetry message to indicate successful Encoder reset
        telemetry.addLine("Waiting for start");
        telemetry.update();

        waitForStart();
//***********************************************************************************************************
//***********************************************************************************************************
//*******Blue Right******************************************************************************************

        liftMove(2000);
        encoderDrive(DRIVE_SPEED,   32, 32, 0, 0);  // drive to blocks
        
        liftMove(250);
        ClawDown(true);
        sleep(50);
        liftMove(1200);


        encoderDrive(DRIVE_SPEED, -7, -7, 0, 0);
        encoderDrive(DRIVE_SPEED, 1, 0, 0, 0);

        encoderDrive(DRIVE_SPEED, 0,0 ,40, 40); //+ distance --> left

        ClawUp();

        encoderDrive(DRIVE_SPEED, -5,-5, 0, 0);
        encoderDrive(DRIVE_SPEED, 0, 0,-47, -47); //- distance --> right
        liftMove(2000);
        encoderDrive(DRIVE_SPEED, 13, 13, 0, 0);

        liftMove(250);
        ClawDown(false);
        sleep(50);
        liftMove(1200);


        encoderDrive(DRIVE_SPEED, -7, -7, 0, 0);
        encoderDrive(DRIVE_SPEED, 0,0 ,54, 54); //+ distance --> left
        ClawUp();

        encoderDrive(DRIVE_SPEED, 0, 0,-20, -20); //+ distance --> right
        encoderDrive(DRIVE_SPEED, 2, 2, 0, 0);

        telemetry.addData("Path", "Complete");
        telemetry.update();
    }

    /*
     *  Method to perfmorm a relative move, based on encoder counts.
     *  Encoders are not reset as the move is based on the current position.
     *  Move will stop if any of three conditions occur:
     *  1) Move gets to the desired position
     *  2) Move runs out of time
     *  3) Driver stops the opmode running.
     */
    public void encoderDrive(double speed,
                             double leftInches, double rightInches, double frontInches, double backInches) {
        int newLeftTarget;
        int newRightTarget;
        int newFrontTarget;
        int newBackTarget;


        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftTarget = left.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
            newRightTarget = right.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
            newBackTarget = back.getCurrentPosition() + (int)(backInches * COUNTS_PER_INCH);
            newFrontTarget = front.getCurrentPosition() + (int)(frontInches * COUNTS_PER_INCH);

            left.setTargetPosition(newLeftTarget);
            right.setTargetPosition(newRightTarget);
            front.setTargetPosition(newFrontTarget);
            back.setTargetPosition(newBackTarget);

            // Turn On RUN_TO_POSITION
            left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            front.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            back.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.

            left.setPower(Math.abs(speed));
            right.setPower(Math.abs(speed));
            front.setPower(Math.abs(speed));
            back.setPower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&

                    (left.isBusy() || right.isBusy() || front.isBusy() || back.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Path1",  "Running to %7d :%7d :%7d: %7d", newLeftTarget,  newRightTarget, newFrontTarget, newBackTarget);
                telemetry.addData("Path2",  "Running at %7d :%7d :%7d: %7d",
                        left.getCurrentPosition(),
                        right.getCurrentPosition(),
                        front.getCurrentPosition(),
                        back.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            left.setPower(0);
            right.setPower(0);
            front.setPower(0);
            back.setPower(0);

            // Turn off RUN_TO_POSITION
            left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            front.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            back.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            //  sleep(250);   // optional pause after each move
        }

    }


    public void encoderTurn (  double speed, double angle) {
        telemetry.addData("Degrees: ", angle);

        double fullRotation = 1680 * 3;
        double turnFract = 360 / Math.abs(angle);
        telemetry.addData("turn fraction: ", turnFract);

        int newLeftTarget = 0, newRightTarget = 0;

        double countNum = fullRotation / turnFract;
        telemetry.addData("encoder count", countNum);

        left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        front.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        back.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        if (angle > 0) {
            newLeftTarget = left.getCurrentPosition() - (int) countNum;
            newRightTarget = right.getCurrentPosition() + (int) countNum;
            left.setTargetPosition(newLeftTarget);
            right.setTargetPosition(newRightTarget);

            newLeftTarget = back.getCurrentPosition() + (int) countNum;
            newRightTarget = front.getCurrentPosition() - (int) countNum;
            back.setTargetPosition(newLeftTarget);
            front.setTargetPosition(newRightTarget);
        }

        if (angle < 0) {
            newLeftTarget = left.getCurrentPosition() + (int) countNum;
            newRightTarget = right.getCurrentPosition() - (int) countNum;
            left.setTargetPosition(newLeftTarget);
            right.setTargetPosition(newRightTarget);

            newLeftTarget = back.getCurrentPosition() - (int) countNum;
            newRightTarget = front.getCurrentPosition() + (int) countNum;
            back.setTargetPosition(newLeftTarget);
            front.setTargetPosition(newRightTarget);
        }

        left.setPower(speed);
        right.setPower(speed);
        front.setPower(speed);
        back.setPower(speed);

        while (opModeIsActive() && (left.isBusy() && right.isBusy())) {

        }

        // Turn off RUN_TO_POSITION
        left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        front.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        back.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Stop all motion;
        left.setPower(0);
        right.setPower(0);
        front.setPower(0);
        back.setPower(0);

        telemetry.addData("turn", "complete");
        telemetry.update();
        sleep(100);
    }

    
    public void ClawDown(boolean beginning) {
        if(beginning) {
            claw.setPower(1);
            sleep(350);
            claw.setPower(-1);
            sleep(250);
            claw.setPower(-0.1);
        } else {
            claw.setPower(-1);
            sleep(250);
            claw.setPower(-0.1);
        }
    }

    public void ClawUp() {
        claw.setPower(1);
        sleep(250);
        claw.setPower(0);
    }

    public void liftMove(int pos){

        // Ensure that the opmode is still active
        if (opModeIsActive()) {
            lift.setTargetPosition(pos);

            lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            lift.setPower(1);

            while (opModeIsActive() && lift.isBusy()) {

                // Display it for the driver.
                idle();
            }


            lift.setPower(0);



            // Turn off RUN_TO_POSITION
            lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            //  sleep(250);   // optional pause after each move
        }
    }
}
